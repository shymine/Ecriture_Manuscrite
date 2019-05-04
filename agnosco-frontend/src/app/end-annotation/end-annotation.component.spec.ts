import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EndAnnotationComponent } from './end-annotation.component';

describe('EndAnnotationComponent', () => {
  let component: EndAnnotationComponent;
  let fixture: ComponentFixture<EndAnnotationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EndAnnotationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EndAnnotationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
