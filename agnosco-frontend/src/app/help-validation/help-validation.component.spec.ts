import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HelpValidationComponent } from './help-validation.component';

describe('HelpValidationComponent', () => {
  let component: HelpValidationComponent;
  let fixture: ComponentFixture<HelpValidationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HelpValidationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HelpValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
