import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccueilComponent } from './accueil/accueil.component';
import { DecoupeComponent } from './decoupe/decoupe.component';
import { AnnotationComponent } from './annotation/annotation.component';
import { ValidationComponent } from './validation/validation.component';

const routes: Routes = [
  { path: '',
  redirectTo: 'accueil',
  pathMatch: 'full'
  },
  {path: 'accueil', component: AccueilComponent},
  {path: 'decoupe', component: DecoupeComponent},
  {path: 'annotation', component: AnnotationComponent},
  {path: 'validation', component: ValidationComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
