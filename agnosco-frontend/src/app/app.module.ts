import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http'

import { MatDialogModule} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AccueilComponent } from './accueil/accueil.component';
import { DecoupeComponent } from './decoupe/decoupe.component';
import { AnnotationComponent } from './annotation/annotation.component';
import { ValidationComponent } from './validation/validation.component';
import { MydialogComponent } from './mydialog/mydialog.component';
import { AddDocComponent } from './add-doc/add-doc.component';
import { SuppressionDialogComponent } from './suppression-dialog/suppression-dialog.component';
import { GestionPagesComponent } from './gestion-pages/gestion-pages.component';



@NgModule({
  declarations: [
    AppComponent,
    AccueilComponent,
    DecoupeComponent,
    AnnotationComponent,
    ValidationComponent,
    MydialogComponent,
    AddDocComponent,
    SuppressionDialogComponent,
    GestionPagesComponent
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
