import { TestBed } from '@angular/core/testing';

import { PageDataService } from './pageData.service';

describe('ValidationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PageDataService = TestBed.get(PageDataService);
    expect(service).toBeTruthy();
  });
});
