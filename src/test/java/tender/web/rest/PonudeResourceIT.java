package tender.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tender.IntegrationTest;
import tender.domain.Ponude;
import tender.repository.PonudeRepository;
import tender.service.criteria.PonudeCriteria;

/**
 * Integration tests for the {@link PonudeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PonudeResourceIT {

    private static final Integer DEFAULT_SIFRA_PONUDE = 1;
    private static final Integer UPDATED_SIFRA_PONUDE = 2;
    private static final Integer SMALLER_SIFRA_PONUDE = 1 - 1;

    private static final Double DEFAULT_PONUDJENA_VRIJEDNOST = 1D;
    private static final Double UPDATED_PONUDJENA_VRIJEDNOST = 2D;
    private static final Double SMALLER_PONUDJENA_VRIJEDNOST = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/ponudes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PonudeRepository ponudeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPonudeMockMvc;

    private Ponude ponude;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ponude createEntity(EntityManager em) {
        Ponude ponude = new Ponude().sifraPonude(DEFAULT_SIFRA_PONUDE).ponudjenaVrijednost(DEFAULT_PONUDJENA_VRIJEDNOST);
        return ponude;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ponude createUpdatedEntity(EntityManager em) {
        Ponude ponude = new Ponude().sifraPonude(UPDATED_SIFRA_PONUDE).ponudjenaVrijednost(UPDATED_PONUDJENA_VRIJEDNOST);
        return ponude;
    }

    @BeforeEach
    public void initTest() {
        ponude = createEntity(em);
    }

    @Test
    @Transactional
    void createPonude() throws Exception {
        int databaseSizeBeforeCreate = ponudeRepository.findAll().size();
        // Create the Ponude
        restPonudeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isCreated());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeCreate + 1);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPonude()).isEqualTo(DEFAULT_SIFRA_PONUDE);
        assertThat(testPonude.getPonudjenaVrijednost()).isEqualTo(DEFAULT_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void createPonudeWithExistingId() throws Exception {
        // Create the Ponude with an existing ID
        ponude.setId(1L);

        int databaseSizeBeforeCreate = ponudeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPonudeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPonudes() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ponude.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPonude").value(hasItem(DEFAULT_SIFRA_PONUDE)))
            .andExpect(jsonPath("$.[*].ponudjenaVrijednost").value(hasItem(DEFAULT_PONUDJENA_VRIJEDNOST.doubleValue())));
    }

    @Test
    @Transactional
    void getPonude() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get the ponude
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL_ID, ponude.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ponude.getId().intValue()))
            .andExpect(jsonPath("$.sifraPonude").value(DEFAULT_SIFRA_PONUDE))
            .andExpect(jsonPath("$.ponudjenaVrijednost").value(DEFAULT_PONUDJENA_VRIJEDNOST.doubleValue()));
    }

    @Test
    @Transactional
    void getPonudesByIdFiltering() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        Long id = ponude.getId();

        defaultPonudeShouldBeFound("id.equals=" + id);
        defaultPonudeShouldNotBeFound("id.notEquals=" + id);

        defaultPonudeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPonudeShouldNotBeFound("id.greaterThan=" + id);

        defaultPonudeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPonudeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPonudeIsEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPonude equals to DEFAULT_SIFRA_PONUDE
        defaultPonudeShouldBeFound("sifraPonude.equals=" + DEFAULT_SIFRA_PONUDE);

        // Get all the ponudeList where sifraPonude equals to UPDATED_SIFRA_PONUDE
        defaultPonudeShouldNotBeFound("sifraPonude.equals=" + UPDATED_SIFRA_PONUDE);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPonudeIsInShouldWork() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPonude in DEFAULT_SIFRA_PONUDE or UPDATED_SIFRA_PONUDE
        defaultPonudeShouldBeFound("sifraPonude.in=" + DEFAULT_SIFRA_PONUDE + "," + UPDATED_SIFRA_PONUDE);

        // Get all the ponudeList where sifraPonude equals to UPDATED_SIFRA_PONUDE
        defaultPonudeShouldNotBeFound("sifraPonude.in=" + UPDATED_SIFRA_PONUDE);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPonudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPonude is not null
        defaultPonudeShouldBeFound("sifraPonude.specified=true");

        // Get all the ponudeList where sifraPonude is null
        defaultPonudeShouldNotBeFound("sifraPonude.specified=false");
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPonudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPonude is greater than or equal to DEFAULT_SIFRA_PONUDE
        defaultPonudeShouldBeFound("sifraPonude.greaterThanOrEqual=" + DEFAULT_SIFRA_PONUDE);

        // Get all the ponudeList where sifraPonude is greater than or equal to UPDATED_SIFRA_PONUDE
        defaultPonudeShouldNotBeFound("sifraPonude.greaterThanOrEqual=" + UPDATED_SIFRA_PONUDE);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPonudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPonude is less than or equal to DEFAULT_SIFRA_PONUDE
        defaultPonudeShouldBeFound("sifraPonude.lessThanOrEqual=" + DEFAULT_SIFRA_PONUDE);

        // Get all the ponudeList where sifraPonude is less than or equal to SMALLER_SIFRA_PONUDE
        defaultPonudeShouldNotBeFound("sifraPonude.lessThanOrEqual=" + SMALLER_SIFRA_PONUDE);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPonudeIsLessThanSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPonude is less than DEFAULT_SIFRA_PONUDE
        defaultPonudeShouldNotBeFound("sifraPonude.lessThan=" + DEFAULT_SIFRA_PONUDE);

        // Get all the ponudeList where sifraPonude is less than UPDATED_SIFRA_PONUDE
        defaultPonudeShouldBeFound("sifraPonude.lessThan=" + UPDATED_SIFRA_PONUDE);
    }

    @Test
    @Transactional
    void getAllPonudesBySifraPonudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where sifraPonude is greater than DEFAULT_SIFRA_PONUDE
        defaultPonudeShouldNotBeFound("sifraPonude.greaterThan=" + DEFAULT_SIFRA_PONUDE);

        // Get all the ponudeList where sifraPonude is greater than SMALLER_SIFRA_PONUDE
        defaultPonudeShouldBeFound("sifraPonude.greaterThan=" + SMALLER_SIFRA_PONUDE);
    }

    @Test
    @Transactional
    void getAllPonudesByPonudjenaVrijednostIsEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where ponudjenaVrijednost equals to DEFAULT_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldBeFound("ponudjenaVrijednost.equals=" + DEFAULT_PONUDJENA_VRIJEDNOST);

        // Get all the ponudeList where ponudjenaVrijednost equals to UPDATED_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldNotBeFound("ponudjenaVrijednost.equals=" + UPDATED_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void getAllPonudesByPonudjenaVrijednostIsInShouldWork() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where ponudjenaVrijednost in DEFAULT_PONUDJENA_VRIJEDNOST or UPDATED_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldBeFound("ponudjenaVrijednost.in=" + DEFAULT_PONUDJENA_VRIJEDNOST + "," + UPDATED_PONUDJENA_VRIJEDNOST);

        // Get all the ponudeList where ponudjenaVrijednost equals to UPDATED_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldNotBeFound("ponudjenaVrijednost.in=" + UPDATED_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void getAllPonudesByPonudjenaVrijednostIsNullOrNotNull() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where ponudjenaVrijednost is not null
        defaultPonudeShouldBeFound("ponudjenaVrijednost.specified=true");

        // Get all the ponudeList where ponudjenaVrijednost is null
        defaultPonudeShouldNotBeFound("ponudjenaVrijednost.specified=false");
    }

    @Test
    @Transactional
    void getAllPonudesByPonudjenaVrijednostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where ponudjenaVrijednost is greater than or equal to DEFAULT_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldBeFound("ponudjenaVrijednost.greaterThanOrEqual=" + DEFAULT_PONUDJENA_VRIJEDNOST);

        // Get all the ponudeList where ponudjenaVrijednost is greater than or equal to UPDATED_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldNotBeFound("ponudjenaVrijednost.greaterThanOrEqual=" + UPDATED_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void getAllPonudesByPonudjenaVrijednostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where ponudjenaVrijednost is less than or equal to DEFAULT_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldBeFound("ponudjenaVrijednost.lessThanOrEqual=" + DEFAULT_PONUDJENA_VRIJEDNOST);

        // Get all the ponudeList where ponudjenaVrijednost is less than or equal to SMALLER_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldNotBeFound("ponudjenaVrijednost.lessThanOrEqual=" + SMALLER_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void getAllPonudesByPonudjenaVrijednostIsLessThanSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where ponudjenaVrijednost is less than DEFAULT_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldNotBeFound("ponudjenaVrijednost.lessThan=" + DEFAULT_PONUDJENA_VRIJEDNOST);

        // Get all the ponudeList where ponudjenaVrijednost is less than UPDATED_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldBeFound("ponudjenaVrijednost.lessThan=" + UPDATED_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void getAllPonudesByPonudjenaVrijednostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        // Get all the ponudeList where ponudjenaVrijednost is greater than DEFAULT_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldNotBeFound("ponudjenaVrijednost.greaterThan=" + DEFAULT_PONUDJENA_VRIJEDNOST);

        // Get all the ponudeList where ponudjenaVrijednost is greater than SMALLER_PONUDJENA_VRIJEDNOST
        defaultPonudeShouldBeFound("ponudjenaVrijednost.greaterThan=" + SMALLER_PONUDJENA_VRIJEDNOST);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPonudeShouldBeFound(String filter) throws Exception {
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ponude.getId().intValue())))
            .andExpect(jsonPath("$.[*].sifraPonude").value(hasItem(DEFAULT_SIFRA_PONUDE)))
            .andExpect(jsonPath("$.[*].ponudjenaVrijednost").value(hasItem(DEFAULT_PONUDJENA_VRIJEDNOST.doubleValue())));

        // Check, that the count call also returns 1
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPonudeShouldNotBeFound(String filter) throws Exception {
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPonudeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPonude() throws Exception {
        // Get the ponude
        restPonudeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPonude() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();

        // Update the ponude
        Ponude updatedPonude = ponudeRepository.findById(ponude.getId()).get();
        // Disconnect from session so that the updates on updatedPonude are not directly saved in db
        em.detach(updatedPonude);
        updatedPonude.sifraPonude(UPDATED_SIFRA_PONUDE).ponudjenaVrijednost(UPDATED_PONUDJENA_VRIJEDNOST);

        restPonudeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPonude.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPonude))
            )
            .andExpect(status().isOk());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPonude()).isEqualTo(UPDATED_SIFRA_PONUDE);
        assertThat(testPonude.getPonudjenaVrijednost()).isEqualTo(UPDATED_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void putNonExistingPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ponude.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePonudeWithPatch() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();

        // Update the ponude using partial update
        Ponude partialUpdatedPonude = new Ponude();
        partialUpdatedPonude.setId(ponude.getId());

        partialUpdatedPonude.sifraPonude(UPDATED_SIFRA_PONUDE);

        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPonude.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPonude))
            )
            .andExpect(status().isOk());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPonude()).isEqualTo(UPDATED_SIFRA_PONUDE);
        assertThat(testPonude.getPonudjenaVrijednost()).isEqualTo(DEFAULT_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void fullUpdatePonudeWithPatch() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();

        // Update the ponude using partial update
        Ponude partialUpdatedPonude = new Ponude();
        partialUpdatedPonude.setId(ponude.getId());

        partialUpdatedPonude.sifraPonude(UPDATED_SIFRA_PONUDE).ponudjenaVrijednost(UPDATED_PONUDJENA_VRIJEDNOST);

        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPonude.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPonude))
            )
            .andExpect(status().isOk());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
        Ponude testPonude = ponudeList.get(ponudeList.size() - 1);
        assertThat(testPonude.getSifraPonude()).isEqualTo(UPDATED_SIFRA_PONUDE);
        assertThat(testPonude.getPonudjenaVrijednost()).isEqualTo(UPDATED_PONUDJENA_VRIJEDNOST);
    }

    @Test
    @Transactional
    void patchNonExistingPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ponude.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ponude))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPonude() throws Exception {
        int databaseSizeBeforeUpdate = ponudeRepository.findAll().size();
        ponude.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPonudeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ponude)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ponude in the database
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePonude() throws Exception {
        // Initialize the database
        ponudeRepository.saveAndFlush(ponude);

        int databaseSizeBeforeDelete = ponudeRepository.findAll().size();

        // Delete the ponude
        restPonudeMockMvc
            .perform(delete(ENTITY_API_URL_ID, ponude.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ponude> ponudeList = ponudeRepository.findAll();
        assertThat(ponudeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
