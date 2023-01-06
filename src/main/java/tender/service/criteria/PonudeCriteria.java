package tender.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link tender.domain.Ponude} entity. This class is used
 * in {@link tender.web.rest.PonudeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ponudes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PonudeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter sifraPonude;

    private DoubleFilter ponudjenaVrijednost;

    private Boolean distinct;

    public PonudeCriteria() {}

    public PonudeCriteria(PonudeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.sifraPonude = other.sifraPonude == null ? null : other.sifraPonude.copy();
        this.ponudjenaVrijednost = other.ponudjenaVrijednost == null ? null : other.ponudjenaVrijednost.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PonudeCriteria copy() {
        return new PonudeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getSifraPonude() {
        return sifraPonude;
    }

    public IntegerFilter sifraPonude() {
        if (sifraPonude == null) {
            sifraPonude = new IntegerFilter();
        }
        return sifraPonude;
    }

    public void setSifraPonude(IntegerFilter sifraPonude) {
        this.sifraPonude = sifraPonude;
    }

    public DoubleFilter getPonudjenaVrijednost() {
        return ponudjenaVrijednost;
    }

    public DoubleFilter ponudjenaVrijednost() {
        if (ponudjenaVrijednost == null) {
            ponudjenaVrijednost = new DoubleFilter();
        }
        return ponudjenaVrijednost;
    }

    public void setPonudjenaVrijednost(DoubleFilter ponudjenaVrijednost) {
        this.ponudjenaVrijednost = ponudjenaVrijednost;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PonudeCriteria that = (PonudeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sifraPonude, that.sifraPonude) &&
            Objects.equals(ponudjenaVrijednost, that.ponudjenaVrijednost) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sifraPonude, ponudjenaVrijednost, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PonudeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (sifraPonude != null ? "sifraPonude=" + sifraPonude + ", " : "") +
            (ponudjenaVrijednost != null ? "ponudjenaVrijednost=" + ponudjenaVrijednost + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
