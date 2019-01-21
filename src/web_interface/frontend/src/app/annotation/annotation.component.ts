import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-annotation',
  templateUrl: './annotation.component.html',
  styleUrls: ['./annotation.component.css']
})
export class AnnotationComponent implements OnInit {

  private params=[];

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
