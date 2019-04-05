import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidateDialogComponent } from './validate-dialog.component';

describe('ValidateDialogComponent', () => {
  let component: ValidateDialogComponent;
  let fixture: ComponentFixture<ValidateDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ValidateDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ValidateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
