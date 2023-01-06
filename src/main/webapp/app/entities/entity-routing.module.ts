import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ponude',
        data: { pageTitle: 'testApp.ponude.home.title' },
        loadChildren: () => import('./ponude/ponude.module').then(m => m.PonudeModule),
      },
      {
        path: 'student',
        data: { pageTitle: 'testApp.student.home.title' },
        loadChildren: () => import('./student/student.module').then(m => m.StudentModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
