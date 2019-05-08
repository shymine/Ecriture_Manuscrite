import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccueilComponent } from './accueil/accueil.component';
import { AnnotationComponent } from './annotation/annotation.component';
import { ValidationComponent } from './validation/validation.component';
import { MydialogComponent } from './mydialog/mydialog.component';
import { ValidateDialogComponent} from './validate-dialog/validate-dialog.component';
import { HelpValidationComponent} from './help-validation/help-validation.component';
import { HelpAnnotationComponent} from './help-annotation/help-annotation.component';
import { AddDocComponent } from './add-doc/add-doc.component';
import { SuppressionDialogComponent } from './suppression-dialog/suppression-dialog.component';
import { GestionPagesComponent } from './gestion-pages/gestion-pages.component';
import { ExportProjetComponent } from './export-projet/export-projet.component';
import { EndValidationComponent} from './end-validation/end-validation.component';


const routes: Routes = [
  { path: '',
  redirectTo: 'accueil',
  pathMatch: 'full'
  },
  {path: 'accueil', component: AccueilComponent},
  {path: 'annotation', component: AnnotationComponent},
  {path: 'validation', component: ValidationComponent},
  {path: 'mydialog', component: MydialogComponent},
  {path: 'validate-dialog', component:ValidateDialogComponent},
  {path: 'add-doc', component: AddDocComponent},
  {path: 'suppression-dialog', component: SuppressionDialogComponent},
  {path: 'gestion-pages', component: GestionPagesComponent},
  {path: 'export-projet', component: ExportProjetComponent},
  {path: 'help-validation', component: HelpValidationComponent},
  {path: 'help-annotation', component: HelpAnnotationComponent},
  {path: 'end-validation', component: EndValidationComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
