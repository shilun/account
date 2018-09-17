import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {RouterModule, Routes} from '@angular/router';
import {SharedModule} from '../../shared/shared.module';
import {NgxDatatableModule} from '@swimlane/ngx-datatable';
import {Ng2TableModule} from 'ng2-table';
import {AccountListComponent} from "./account-list/account-list.component";
import {ConfigListComponent} from "./config-list/config-list.component";
import {ConfigViewComponent} from "./config-view/config-view.component";


const routes: Routes = [

  {path: 'account/list', component: AccountListComponent},
  {path: 'config/list', component: ConfigListComponent},
  {path: 'config/view', component: ConfigViewComponent}
];

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes),
    Ng2TableModule,
    NgxDatatableModule
  ],
  declarations: [
    AccountListComponent,ConfigListComponent,ConfigViewComponent]
})


export class SystemModule {
}
