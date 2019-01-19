import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DecoupeComponent } from './decoupe.component';

describe('DecoupeComponent', () => {
  let component: DecoupeComponent;
  let fixture: ComponentFixture<DecoupeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DecoupeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DecoupeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
