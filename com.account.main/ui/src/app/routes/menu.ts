export const menu = [
  {
    text: '分析',
    heading: true
  },
  {
    text: '仪表盘',
    link: '/dashboard/v1'
  },
  {
    text: '功能',
    heading: true
  },
  {
    text: '系统管理',
    link: '/system',
    icon: 'fa fa-gears',
    submenu: [{
      text: '用户账本管理',
      link: '/system/account/list'
    }]
  }
];
