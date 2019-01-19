import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-validation',
  templateUrl: './validation.component.html',
  styleUrls: ['./validation.component.css']
})
export class ValidationComponent implements OnInit {

  private params = [];

  constructor(private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit() {
    //parametres
    this.route.params.forEach(param => {
      this.params.push(param.id);
    });
  }

  goHome(){
    this.router.navigate(['']);
  }
}
