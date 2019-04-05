import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SuppressionDialogComponent } from './suppression-dialog.component';

describe('SuppressionDialogComponent', () => {
  let component: SuppressionDialogComponent;
  let fixture: ComponentFixture<SuppressionDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SuppressionDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SuppressionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
