import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractController} from '../../../common/abstract.controller';
import {GlobalService} from "../../../services/global.service";
import {ConfigService} from '../../../services/config.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './config-list.component.html'
})
export class ConfigListComponent extends AbstractController implements OnInit {
  statuses:any;

  public pageIndex: number = 0;
  constructor(protected configService: ConfigService, protected globalService: GlobalService, protected route: ActivatedRoute, protected router: Router) {
    super(configService, route, router);
    this.entity = {};
    this.pageSize=30;
  }

  async list(query): Promise<void> {
    return super.list(query);
  }

  async ngOnInit() {
    await this.list(this.entity);
    let result = await this.globalService.list('yesorno');
    if (result.success) {
      this.statuses = result.data.list;
    }
  }

}
