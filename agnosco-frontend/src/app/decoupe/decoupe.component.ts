import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-decoupe',
  templateUrl: './decoupe.component.html',
  styleUrls: ['./decoupe.component.css']
})
export class DecoupeComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }

  goHome(){
    this.router.navigate(['']);
  }
}
