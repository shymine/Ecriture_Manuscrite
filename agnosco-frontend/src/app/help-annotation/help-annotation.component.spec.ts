import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HelpAnnotationComponent } from './help-annotation.component';

describe('HelpValidationComponent', () => {
  let component: HelpAnnotationComponent;
  let fixture: ComponentFixture<HelpAnnotationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HelpAnnotationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HelpAnnotationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
