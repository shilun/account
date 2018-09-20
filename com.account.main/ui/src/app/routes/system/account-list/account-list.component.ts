import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractController} from '../../../common/abstract.controller';
import {GlobalService} from "../../../services/global.service";
import {AccountService} from "../../../services/account.service";

@Component({
  selector: 'app-user-list',
  templateUrl: './account-list.component.html'
})
export class AccountListComponent extends AbstractController implements OnInit {
  statuses: any;
  proxyes: any;
  tokentypes:any;
  public pageSize: number = 10;
  public pageIndex: number = 0;

  constructor(protected userService: AccountService, protected globalService: GlobalService, protected route: ActivatedRoute, protected router: Router) {
    super(userService, route, router);
    this.entity = {};
  }

  async ngOnInit() {
    await this.list(this.entity);
    let result = await this.globalService.list('yesorno');
    if (result.success) {
      this.statuses = result.data.list;
    }
    result = await this.globalService.list('proxy');
    if (result.success) {
      this.proxyes = result.data.list;
    }
    result = await this.globalService.list('tokentype');
    if (result.success) {
      this.tokentypes = result.data.list;
    }
  }

}
