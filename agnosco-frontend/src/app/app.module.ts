import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http'

import { MatDialogModule} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AccueilComponent } from './accueil/accueil.component';
import { AnnotationComponent } from './annotation/annotation.component';
import { ValidationComponent } from './validation/validation.component';
import { MydialogComponent } from './mydialog/mydialog.component';
import { ValidateDialogComponent} from './validate-dialog/validate-dialog.component';
import { AddDocComponent } from './add-doc/add-doc.component';
import { SuppressionDialogComponent } from './suppression-dialog/suppression-dialog.component';
import { GestionPagesComponent } from './gestion-pages/gestion-pages.component';
import { ExportProjetComponent } from './export-projet/export-projet.component';
import { EndValidationComponent} from './end-validation/end-validation.component';
import { EndAnnotationComponent} from './end-annotation/end-annotation.component';

import { HelpValidationComponent } from './help-validation/help-validation.component';
import { HelpAnnotationComponent} from './help-annotation/help-annotation.component';



@NgModule({
  declarations: [
    AppComponent,
    AccueilComponent,
    AnnotationComponent,
    ValidationComponent,
    MydialogComponent,
    ValidateDialogComponent,
    AddDocComponent,
    SuppressionDialogComponent,
    GestionPagesComponent,
    ExportProjetComponent,
    HelpAnnotationComponent,
    HelpValidationComponent,
    EndValidationComponent,
    EndAnnotationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatDialogModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
