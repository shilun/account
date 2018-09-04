import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractController} from '../../../common/abstract.controller';
import {UserService} from '../../../services/user.service';
import {GlobalService} from "../../../services/global.service";

@Component({
  selector: 'app-user-list',
  templateUrl: './account-list.component.html'
})
export class AccountListComponent extends AbstractController implements OnInit {
  statuses:any;
  constructor(protected userService: UserService,protected globalService: GlobalService,  protected route: ActivatedRoute, protected router: Router) {
    super(userService, route, router);
    this.entity = {};
  }

  async ngOnInit() {
    this.list(this.entity);
    let result = await this.globalService.list('yesorno');
    if (result.success) {
      this.statuses = result.data.list;
    }
  }

}
