import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListeUsersComponent } from './liste-users.component';

describe('ListeUsersComponent', () => {
  let component: ListeUsersComponent;
  let fixture: ComponentFixture<ListeUsersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListeUsersComponent]
    });
    fixture = TestBed.createComponent(ListeUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
