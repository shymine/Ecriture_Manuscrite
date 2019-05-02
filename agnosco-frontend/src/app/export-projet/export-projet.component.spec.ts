import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportProjetComponent } from './export-projet.component';

describe('ExportProjetComponent', () => {
  let component: ExportProjetComponent;
  let fixture: ComponentFixture<ExportProjetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExportProjetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExportProjetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
