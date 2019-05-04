import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EndValidationComponent } from './end-validation.component';

describe('EndValidationComponent', () => {
  let component: EndValidationComponent;
  let fixture: ComponentFixture<EndValidationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EndValidationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EndValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
