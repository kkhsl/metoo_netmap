(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2e5153ad","chunk-150359ea"],{"0970":function(e,t,n){"use strict";n("a4f8")},"24b4":function(e,t,n){"use strict";n.d(t,"e",(function(){return r})),n.d(t,"g",(function(){return a})),n.d(t,"h",(function(){return s})),n.d(t,"b",(function(){return o})),n.d(t,"c",(function(){return c})),n.d(t,"d",(function(){return l})),n.d(t,"j",(function(){return u})),n.d(t,"f",(function(){return d})),n.d(t,"i",(function(){return p})),n.d(t,"a",(function(){return m}));var i=n("b775");function r(e){return Object(i["a"])({url:"/admin/gather/type",method:"get",params:e})}function a(e){return Object(i["a"])({url:"/admin/gather/main",method:"get",params:e})}function s(e){return Object(i["a"])({url:"/admin/gather/stop",method:"post",data:e})}function o(e){return Object(i["a"])({url:"/admin/sureying/log",method:"get",params:e})}function c(e){return Object(i["a"])({url:"/admin/sureying/log/single/segment",method:"get",params:e})}function l(e){return Object(i["a"])({url:"/admin/gather/log/status",method:"get",params:e})}function u(){return Object(i["a"])({url:"/admin/area/all",method:"get"})}function d(e){return Object(i["a"])({url:"/admin/unit/subnet/list",method:"post",data:e})}function p(e){return Object(i["a"])({url:"/admin/unit/subnet/batch",method:"post",data:e})}function m(e){return Object(i["a"])({url:"/admin/unit/subnet",method:"DELETE",params:e})}},"3bb4":function(e,t,n){"use strict";n.r(t);var i=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-card",{scopedSlots:e._u([{key:"header",fn:function(){return[n("div",{staticClass:"clearfix"},[n("span",{staticStyle:{"line-height":"28px"}},[e._v("测绘日志")]),n("div",{staticClass:"fr"},[n("el-button",{attrs:{size:"small",type:"primary",disabled:e.isRunning},on:{click:e.handBtn}},[e.isRunning?n("span",[n("i",{staticClass:"el-icon-loading"}),e._v(" 测绘中...")]):n("span",[e._v("开始测绘")])]),n("el-button",{attrs:{size:"small",type:"success"},on:{click:function(t){e.viewDialogFlag=!0}}},[e._v("测绘结果")])],1)])]},proxy:!0}])},[n("div",{directives:[{name:"loading",rawName:"v-loading",value:e.startLoading,expression:"startLoading"}],staticClass:"logWrap fz12"},[e._l(e.logList,(function(t){return[n("div",{key:t.id,staticClass:"logItem"},[n("div",{staticClass:"flexBox"},[4==t.type?[n("div",{staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("div",[e._v(e._s(t.name))])])])]:n("div",{staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("p",[e._v(e._s(t.name))]),n("div",{staticClass:"time"},[n("span",[e._v(e._s(t.beginTime))]),e._v(" ~ "),n("span",[n("span",{directives:[{name:"show",rawName:"v-show",value:1===t.status,expression:"item.status === 1"}],staticClass:"successC"},[n("i",{staticClass:"el-icon-loading"}),n("span",{staticClass:"fz12"},[e._v(e._s("数据上传"===t.name?"数据上传中...":"分析中..."))])]),e._v(" "+e._s(t.endTime)+" ")])])]),n("div",{directives:[{name:"show",rawName:"v-show",value:2===t.status,expression:"item.status === 2"}],staticClass:"dot green"},[e._v("已完成")]),n("div",{directives:[{name:"show",rawName:"v-show",value:3===t.status,expression:"item.status === 3"}],staticClass:"dot red"},[e._v("失败")])]),n("div",{staticClass:"actions"})],2),t.subLogs?n("div",{staticClass:"subLogWrap p-l-10"},e._l(t.subLogs,(function(t){return n("div",{key:t.id,staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("p",{staticStyle:{color:"#7b7b7bbf","text-align":"center"}},[e._v(e._s(t.name))]),n("div",{staticClass:"time"},[n("span",[e._v(e._s(t.beginTime))]),e._v(" ~ "),n("span",[n("span",{directives:[{name:"show",rawName:"v-show",value:1===t.status,expression:"subItem.status === 1"}],staticClass:"successC"},[n("i",{staticClass:"el-icon-loading"}),n("span",{staticClass:"fz12"},[e._v(e._s("数据上传"===t.name?"数据上传中...":"分析中..."))])]),e._v(" "+e._s(t.endTime)+" ")])])]),n("div",{directives:[{name:"show",rawName:"v-show",value:2===t.status,expression:"subItem.status === 2"}],staticClass:"dot green"},[e._v("已完成")]),n("div",{directives:[{name:"show",rawName:"v-show",value:3===t.status,expression:"subItem.status === 3"}],staticClass:"dot red"},[e._v("失败")])])})),0):e._e()])]})),n("div",{directives:[{name:"show",rawName:"v-show",value:!e.isRunning&&e.logList.length,expression:"!isRunning && logList.length"}],staticClass:"logItem flexBox"},[n("div",{staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("p",[e._v("测绘完成")])])])])],2),n("el-dialog",{attrs:{"close-on-click-modal":!1,title:"测绘完成","custom-class":"mw600 smTank",visible:e.viewDialogFlag,width:"80%",top:"20%"},on:{"update:visible":function(t){e.viewDialogFlag=t},open:e.getAsyncList}},[n("div",{directives:[{name:"loading",rawName:"v-loading",value:e.detailIsLoading,expression:"detailIsLoading"}]},[n("fieldset",{staticClass:"m-b-10"},[n("el-descriptions",{staticClass:"fz12",attrs:{column:1}},[n("el-descriptions-item",{attrs:{label:"IPv4终端"}},[e._v(e._s(e.rowDetail.ipv4))]),n("el-descriptions-item",{attrs:{label:"IPv4/IPv6终端"}},[e._v(e._s(e.rowDetail.ipv4_ipv6))])],1)],1),n("p",{staticClass:"infoC fz12"},[e._v("测绘结果文件 C:\\output\\目录")])])])],1)},r=[],a=n("c7eb"),s=n("1da1"),o=n("24b4"),c=n("ae6f"),l={data:function(){return{viewTablePost:{currentPage:1,pageSize:15},isRunning:!1,startLoading:!1,logTimer:null,logList:[],viewDialogFlag:!1,detailIsLoading:!1,rowDetail:{result:"",ipv4:"",ipv4_ipv6:"",subme_ipv4:[],subme_ipv6:[],gatewayInfo:[]}}},mounted:function(){this.getCaiJiStatus(),this.getAsyncList()},methods:{getCaiJiStatus:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n,i;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(o["g"])({type:1});case 2:if(n=t.sent,1002!==n.code){t.next=8;break}e.isRunning=!0,e.getLogList(),t.next=13;break;case 8:return t.next=10,Object(o["b"])();case 10:i=t.sent,200==i.code&&i.data&&(e.logList=i.data.data),e.isRunning=!1;case 13:case"end":return t.stop()}}),t)})))()},stopBtn:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(o["h"])();case 2:n=t.sent,200===n.code?(e.logTimer&&clearTimeout(e.logTimer),e.getCaiJiStatus(),e.$message.success("停止成功")):e.$message.error(n.msg);case 4:case"end":return t.stop()}}),t)})))()},handBtn:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(!e.isRunning){t.next=2;break}return t.abrupt("return",e.$message({message:"正在采集中... 请勿重复提交",type:"warning"}));case 2:Object(o["g"])(),setTimeout((function(){e.getCaiJiStatus()}),500);case 4:case"end":return t.stop()}}),t)})))()},getLogList:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(o["b"])();case 2:if(n=t.sent,200!=n.code||!n.data){t.next=13;break}if(e.logList=n.data.data,!n.data.finish){t.next=10;break}return e.isRunning=!1,t.abrupt("return");case 10:e.isRunning=!0,e.logTimer&&clearTimeout(e.logTimer),e.logTimer=setTimeout((function(){e.getLogList()}),2e3);case 13:e.startLoading=!1;case 14:case"end":return t.stop()}}),t)})))()},getAsyncList:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n,i,r;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return e.detailIsLoading=!0,t.next=3,Object(c["a"])(e.viewTablePost);case 3:n=t.sent,200==n.code&&n.data&&(i=n.data.obj[0]||{},r=i.details&&"string"===typeof i.details?JSON.parse(i.details):{},e.rowDetail.result=i.result,e.rowDetail.ipv4=r.ipv4,e.rowDetail.ipv4_ipv6=r.ipv4_ipv6,e.rowDetail.subme_ipv4=r.subme_ipv4&&"string"===typeof r.subme_ipv4?JSON.parse(r.subme_ipv4):[],e.rowDetail.subme_ipv6=r.subme_ipv6&&"string"===typeof r.subme_ipv6?JSON.parse(r.subme_ipv6):[],e.rowDetail.gatewayInfo=r.gatewayInfo),e.detailIsLoading=!1;case 6:case"end":return t.stop()}}),t)})))()}}},u=l,d=(n("0970"),n("2877")),p=Object(d["a"])(u,i,r,!1,null,"ce4530bc",null);t["default"]=p.exports},"3e8d":function(e,t,n){},"703a":function(e,t,n){"use strict";n.r(t);var i=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("el-card",{scopedSlots:e._u([{key:"header",fn:function(){return[n("div",{staticClass:"clearfix"},[n("span",{staticStyle:{"line-height":"28px"}},[e._v("采集设备配置")]),n("div",{staticClass:"fr"},[n("el-button",{attrs:{size:"small",type:"primary",icon:"el-icon-plus"},on:{click:e.addBtn}},[e._v("新增设备")]),n("el-button",{attrs:{size:"small",type:"warning",icon:"el-icon-plus"},on:{click:e.addLogBtn}},[e._v("日志采集")]),1==e.$route.query.type?n("el-button",{attrs:{size:"small",type:"info",plain:""},on:{click:function(t){e.unitIpDialogFlag=!0}}},[e._v(" 政务外网设置 ")]):e._e(),n("el-button",{attrs:{size:"small",type:"info"},on:{click:function(t){return e.$router.push({path:"/"})}}},[e._v("返回首页")])],1)])]},proxy:!0}])},[n("div",{directives:[{name:"loading",rawName:"v-loading",value:e.isLoading,expression:"isLoading"}],staticClass:"fromWrap"},[n("el-empty",{directives:[{name:"show",rawName:"v-show",value:!e.formArr.length,expression:"!formArr.length"}],attrs:{description:"暂无数据..."}}),e._l(e.formArr,(function(t,i){return n("el-form",{key:i,ref:"formItem"+i,refInFor:!0,staticClass:"formItemEle m-b-10 p-b-10 p-l-10 p-r-10",attrs:{model:t,rules:e.rules,"label-position":"top",size:"mini"}},[n("div",{staticClass:"flex between wrap",staticStyle:{"align-items":"self-end"}},[n("el-col",{staticClass:"inputWrap",attrs:{md:18,lg:20}},[n("el-form-item",{attrs:{label:i+1+"# 设备名称",prop:"name"}},[n("el-input",{model:{value:t.name,callback:function(n){e.$set(t,"name","string"===typeof n?n.trim():n)},expression:"formItem.name"}})],1),n("el-form-item",{staticStyle:{width:"160px"},attrs:{label:"设备类型/品牌",prop:"deviceTypeVendorArr"}},[n("el-cascader",{attrs:{options:e.deviceTypeVendorSelectArr,props:{expandTrigger:"hover",children:"deviceVendorList",label:"name",value:"id"}},on:{change:function(n){return e.handleChangeTypeVendor(i,t)}},model:{value:t.deviceTypeVendorArr,callback:function(n){e.$set(t,"deviceTypeVendorArr",n)},expression:"formItem.deviceTypeVendorArr"}})],1),1!=t.type?[n("el-form-item",{attrs:{label:"管理IP",prop:"ip"}},[n("el-input",{model:{value:t.ip,callback:function(n){e.$set(t,"ip","string"===typeof n?n.trim():n)},expression:"formItem.ip"}})],1),n("el-form-item",{attrs:{label:"登陆方式",prop:"loginType"}},[n("el-select",{on:{change:function(n){return e.loginTypeChange(t)}},model:{value:t.loginType,callback:function(n){e.$set(t,"loginType","string"===typeof n?n.trim():n)},expression:"formItem.loginType"}},[n("el-option",{attrs:{value:"ssh"}},[e._v("ssh")]),n("el-option",{attrs:{value:"telnet"}},[e._v("telnet")]),n("el-option",{attrs:{value:"api"}},[e._v("api")])],1)],1),n("el-form-item",{attrs:{label:"端口"}},[n("el-input",{attrs:{placeholder:"ssh"===t.loginType?"22":"23"},model:{value:t.loginPort,callback:function(n){e.$set(t,"loginPort","string"===typeof n?n.trim():n)},expression:"formItem.loginPort"}})],1),n("el-form-item",{attrs:{label:"用户名",prop:"loginName"}},[n("el-input",{attrs:{autocomplete:"new-login"},model:{value:t.loginName,callback:function(n){e.$set(t,"loginName","string"===typeof n?n.trim():n)},expression:"formItem.loginName"}})],1),n("el-form-item",{attrs:{label:"密码",prop:"loginPassword"}},[n("el-input",{attrs:{type:"password",autocomplete:"new-password"},model:{value:t.loginPassword,callback:function(n){e.$set(t,"loginPassword","string"===typeof n?n.trim():n)},expression:"formItem.loginPassword"}})],1),"派网"==t.deviceVendorName?n("el-form-item",{staticStyle:{width:"150px"},attrs:{label:"双栈模式"}},[n("el-select",{attrs:{placeholder:"请选择"},model:{value:t.state,callback:function(n){e.$set(t,"state",n)},expression:"formItem.state"}},[n("el-option",{attrs:{label:"IPv4网关在派网",value:!0}}),n("el-option",{attrs:{label:"IPv4网关不在派网",value:!1}})],1)],1):e._e()]:e._e()],2),1!=t.type?[n("div",[n("p",{staticClass:"textTips fz14",staticStyle:{"margin-bottom":"4px"}},[1===t.testFlag?n("span",{staticClass:"successC"},[e._v("登录正常")]):e._e(),0===t.testFlag?n("span",{staticClass:"dangerC"},[e._v("登录失败")]):e._e()]),n("div",[n("el-button",{directives:[{name:"show",rawName:"v-show",value:0==t.deviceVendorSequence,expression:"formItem.deviceVendorSequence == 0"}],attrs:{plain:"",size:"mini",type:"primary",disabled:t.testLoading},on:{click:function(n){return e.testBtn(t,i)}}},[n("i",{directives:[{name:"show",rawName:"v-show",value:t.testLoading,expression:"formItem.testLoading"}],staticClass:"el-icon-loading"}),e._v(" 测试")]),n("el-button",{attrs:{plain:"",size:"mini",type:"danger"},on:{click:function(n){return e.delRow(t,i)}}},[e._v("删除")])],1)])]:n("div",[n("div",[n("el-button",{attrs:{plain:"",size:"mini",type:"danger"},on:{click:function(n){return e.delRow(t,i)}}},[e._v("删除")])],1)])],2)])}))],2),n("div",{directives:[{name:"show",rawName:"v-show",value:e.formArr.length,expression:"formArr.length"}],staticClass:"flexCC"},[n("el-button",{attrs:{type:"success"},on:{click:e.saveBtn}},[e._v("保存配置")])],1)]),n("hand",{staticClass:"m-t-10"}),n("el-dialog",{attrs:{"close-on-click-modal":!1,title:"单位网段配置","custom-class":"smTank",visible:e.unitIpDialogFlag,width:"60%",top:"10%"},on:{"update:visible":function(t){e.unitIpDialogFlag=t},open:e.onUnitIpConfigOpen}},[n("div",{staticClass:"tipsRow m-b-10"},[e._v("注意：该功能仅为政务外网接入单位划分网段使用")]),n("div",{staticClass:"flexBox p-t-10 p-b-10"},[n("el-row",{staticClass:"flex1"},[n("el-col",{attrs:{span:10}},[n("div",{staticStyle:{color:"#666","font-weight":"bold"}},[e._v("单位名称")])]),n("el-col",{attrs:{span:7}},[n("div",{staticStyle:{color:"#666","font-weight":"bold","text-indent":"12px"}},[e._v("Ipv4网段")])]),n("el-col",{attrs:{span:7}},[n("div",{staticStyle:{color:"#666","font-weight":"bold","text-indent":"12px"}},[e._v("Ipv6网段")])])],1),n("el-button",{staticStyle:{"margin-left":"10px"},attrs:{size:"mini",type:"primary",plain:"",icon:"el-icon-plus"},on:{click:e.addUnitIpBtn}})],1),n("div",{attrs:{id:"unitIpListWrap"}},e._l(e.unitIpFormArr,(function(t,i){return n("el-form",{key:i,ref:"formItem"+i,refInFor:!0,staticClass:"unitForm",attrs:{rules:e.unitIpFormRules,model:t,"label-position":"top",size:"mini"}},[n("div",{staticClass:"flexBox item"},[n("el-row",{staticClass:"flex1",attrs:{gutter:20}},[n("el-col",{attrs:{span:10}},[n("el-form-item",{attrs:{prop:"unitId"}},[n("el-cascader",{staticStyle:{width:"100%"},attrs:{placeholder:"选择单位/可输入单位名称检索",options:e.unitSelectArr,props:{label:"name",value:"id",children:"subAreas",emitPath:!1},filterable:""},model:{value:t.unitId,callback:function(n){e.$set(t,"unitId",n)},expression:"formItem.unitId"}})],1)],1),n("el-col",{attrs:{span:7}},[n("el-form-item",{attrs:{prop:"ipv4Subnet"}},[n("el-input",{attrs:{placeholder:"例: 192.168.1.0/24 (多个网段用,分割)"},model:{value:t.ipv4Subnet,callback:function(n){e.$set(t,"ipv4Subnet","string"===typeof n?n.trim():n)},expression:"formItem.ipv4Subnet"}})],1)],1),n("el-col",{attrs:{span:7}},[n("el-form-item",{attrs:{prop:"ipv6Subnet"}},[n("el-input",{attrs:{placeholder:"例: 2001:db8::/32 (多个网段用,分割)"},model:{value:t.ipv6Subnet,callback:function(n){e.$set(t,"ipv6Subnet","string"===typeof n?n.trim():n)},expression:"formItem.ipv6Subnet"}})],1)],1)],1),n("el-button",{staticStyle:{"margin-left":"10px"},attrs:{size:"mini",type:"info",plain:"",icon:"el-icon-close"},on:{click:function(n){return e.delUnitIpBtn(t,i)}}})],1)])})),1),n("div",{staticClass:"flexCC"},[n("el-button",{attrs:{size:"small",type:"primary"},on:{click:e.saveUnitIpBtn}},[e._v(" 保 存 ")])],1)])],1)},r=[],a=n("c7eb"),s=n("1da1"),o=n("3835"),c=(n("7db0"),n("d3b7"),n("b0c0"),n("d81d"),n("e9c4"),n("159b"),n("a9e3"),n("a434"),n("da71")),l=n("24b4"),u=n("3bb4"),d={components:{hand:u["default"]},data:function(){return{isLoading:!1,formArr:[{name:"",type:0,deviceTypeVendorArr:[],deviceTypeId:"",deviceVendorId:"",deviceVendorSequence:"",deviceModelId:"",ip:"",loginType:"ssh",loginPort:"",loginName:"",loginPassword:"",testLoading:!1,testFlag:null,state:null}],rules:{name:[{required:!0,message:"请输入设备名称",trigger:"blur"}],deviceTypeVendorArr:[{required:!0,message:"请输入设备类型、品牌",trigger:"blur"}],deviceVendorId:[{required:!0,message:"请输入品牌",trigger:"blur"}],ip:[{required:!0,message:"请输入管理IP",trigger:"blur"}],loginType:[{required:!0,message:"请选择登陆方式",trigger:"blur"}],loginName:[{required:!0,message:"请输入用户名",trigger:"blur"}],loginPassword:[{required:!0,message:"请输入密码",trigger:"blur"}]},deviceTypeVendorSelectArr:[],deviceVendorArr:[],unitIpDialogFlag:!1,unitSelectArr:[],unitIpFormArr:[],unitIpFormRules:{unitId:[{required:!0,message:"请选择单位",trigger:"blur"}],ipv4Subnet:[{required:!0,message:"请输入Ipv4网段",trigger:"blur"}],ipv6Subnet:[{required:!0,message:"请输入Ipv6网段",trigger:"blur"}]}}},created:function(){this.getList()},methods:{addBtn:function(){this.formArr.push({id:"",type:0,name:"",deviceTypeVendorArr:[],deviceTypeId:"",deviceVendorId:"",deviceVendorSequence:"",deviceModelId:"",ip:"",loginType:"ssh",loginPort:"",loginName:"",loginPassword:"",testLoading:!1,testFlag:null,state:null})},addLogBtn:function(){this.formArr.push({id:"",type:1,name:"日志采集设备",deviceTypeVendorArr:[],deviceTypeId:"",deviceVendorId:"",deviceVendorSequence:"",deviceModelId:"",ip:"",loginType:"ssh",loginPort:"",loginName:"",loginPassword:"",testLoading:!1,testFlag:null,state:null})},handleChangeTypeVendor:function(e,t){console.log(t.deviceTypeVendorArr);var n=Object(o["a"])(t.deviceTypeVendorArr,2),i=n[0],r=n[1];if(i&&r){this.formArr[e].deviceTypeId=i||"",this.formArr[e].deviceVendorId=r||"";var a=this.deviceTypeVendorSelectArr.find((function(e){return e.id==i})),s=a&&a.deviceVendorList.find((function(e){return e.id==r}));this.formArr[e].deviceVendorSequence=s.sequence,this.formArr[e].deviceVendorName=s.name,"派网"==s.name&&(this.formArr[e].state=!0)}},getList:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n,i,r,s;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return e.isLoading=!0,t.next=3,Object(c["d"])({currentPage:1,pageSize:999});case 3:n=t.sent,200===n.code&&n.data&&(i=n.data,r=i.obj,s=i.other,e.formArr=r.map((function(e){return e.deviceTypeVendorArr=[e.deviceTypeId,e.deviceVendorId],e.testLoading=!1,e.testFlag=null,e})),s&&(e.deviceTypeVendorSelectArr=s.deviceType,e.deviceVendorArr=s.deviceVendor)),e.isLoading=!1;case 6:case"end":return t.stop()}}),t)})))()},loginTypeChange:function(e){var t="";"ssh"===e.loginType?t="22":"telnet"===e.loginType?t="23":"api"===e.loginType&&(t="443"),e.loginPort=t},saveBtn:function(){for(var e=!0,t=0;t<this.formArr.length;t++){var n=this.formArr[t];delete n.createTime,this.$refs["formItem"+t][0].validate(function(){var t=Object(s["a"])(Object(a["a"])().mark((function t(n){return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:n||(e=!1);case 1:case"end":return t.stop()}}),t)})));return function(e){return t.apply(this,arguments)}}())}e&&this.saveDeviceFn()},saveDeviceFn:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n,i;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return n=JSON.parse(JSON.stringify(e.formArr)),n.forEach((function(e){e.loginPort||(e.loginPort="ssh"===e.loginType?"22":"23"),delete e.deviceTypeVendorArr,delete e.testLoading,delete e.testFlag,delete e.deviceVendorSequence})),t.next=4,Object(c["e"])(n);case 4:i=t.sent,200===i.code?(e.$message.success("保存成功"),e.getList()):e.$message.error(i.msg);case 6:case"end":return t.stop()}}),t)})))()},testBtn:function(e,t){var n=this;return Object(s["a"])(Object(a["a"])().mark((function i(){var r;return Object(a["a"])().wrap((function(i){while(1)switch(i.prev=i.next){case 0:if(e.id){i.next=2;break}return i.abrupt("return",n.$message.error("请先保存"));case 2:return e.testLoading=!0,n.formArr[t].testFlag=null,i.next=6,Object(c["g"])({id:e.id});case 6:r=i.sent,200===r.code?n.formArr[t].testFlag=Number(r.data):n.$message.error(r.msg),e.testLoading=!1;case 9:case"end":return i.stop()}}),i)})))()},delRow:function(e,t){var n=this;return Object(s["a"])(Object(a["a"])().mark((function i(){var r;return Object(a["a"])().wrap((function(i){while(1)switch(i.prev=i.next){case 0:if(!e.id){i.next=7;break}return i.next=3,Object(c["a"])({ids:e.id});case 3:r=i.sent,200===r.code?(n.$message.success("删除成功"),n.getList()):n.$message.error(r.msg),i.next=8;break;case 7:n.formArr.splice(t,1);case 8:case"end":return i.stop()}}),i)})))()},onUnitIpConfigOpen:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n,i;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(l["j"])();case 2:return n=t.sent,200==n.code&&(e.unitSelectArr=e.preprocessData(n.data)),t.next=6,Object(l["f"])({currentPage:1,pageSize:999});case 6:i=t.sent,200==i.code&&i.data&&(e.unitIpFormArr=i.data.obj);case 8:case"end":return t.stop()}}),t)})))()},preprocessData:function(e){function t(e){e.subAreas&&e.subAreas.length?e.subAreas.forEach((function(e){t(e)})):delete e.subAreas}return e.forEach((function(e){t(e)})),e},saveUnitIpBtn:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n,i;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:for(n=!0,i=0;i<e.unitIpFormArr.length;i++)e.$refs["formItem"+i][0].validate(function(){var e=Object(s["a"])(Object(a["a"])().mark((function e(t){return Object(a["a"])().wrap((function(e){while(1)switch(e.prev=e.next){case 0:t||(n=!1);case 1:case"end":return e.stop()}}),e)})));return function(t){return e.apply(this,arguments)}}());n&&e.saveUnitIpFn();case 3:case"end":return t.stop()}}),t)})))()},saveUnitIpFn:function(){var e=this;return Object(s["a"])(Object(a["a"])().mark((function t(){var n;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(l["i"])(e.unitIpFormArr);case 2:n=t.sent,200===n.code?(e.$message.success("保存成功"),e.onUnitIpConfigOpen()):e.$message.error(n.msg);case 4:case"end":return t.stop()}}),t)})))()},addUnitIpBtn:function(){this.unitIpFormArr.push({unitId:"",ipv4Subnet:"",ipv6Subnet:""})},delUnitIpBtn:function(e,t){var n=this;return Object(s["a"])(Object(a["a"])().mark((function i(){var r;return Object(a["a"])().wrap((function(i){while(1)switch(i.prev=i.next){case 0:if(!e.id){i.next=7;break}return i.next=3,Object(l["a"])({ids:e.id});case 3:r=i.sent,200===r.code?(n.$message.success("删除成功"),n.onUnitIpConfigOpen()):n.$message.error(r.msg),i.next=8;break;case 7:n.unitIpFormArr.splice(t,1);case 8:case"end":return i.stop()}}),i)})))()}}},p=d,m=(n("f890"),n("2877")),v=Object(m["a"])(p,i,r,!1,null,"702c22c6",null);t["default"]=v.exports},"7db0":function(e,t,n){"use strict";var i=n("23e7"),r=n("b727").find,a=n("44d2"),s=n("ae40"),o="find",c=!0,l=s(o);o in[]&&Array(1)[o]((function(){c=!1})),i({target:"Array",proto:!0,forced:c||!l},{find:function(e){return r(this,e,arguments.length>1?arguments[1]:void 0)}}),a(o)},a4f8:function(e,t,n){},ae6f:function(e,t,n){"use strict";n.d(t,"a",(function(){return r}));var i=n("b775");function r(e){return Object(i["a"])({url:"/admin/gather/log/list",method:"post",data:e})}},da71:function(e,t,n){"use strict";n.d(t,"d",(function(){return r})),n.d(t,"e",(function(){return a})),n.d(t,"g",(function(){return s})),n.d(t,"a",(function(){return o})),n.d(t,"c",(function(){return c})),n.d(t,"f",(function(){return l})),n.d(t,"b",(function(){return u}));var i=n("b775");function r(e){return Object(i["a"])({url:"/admin/device/list",method:"post",data:e})}function a(e){return Object(i["a"])({url:"/admin/device/batch/save",method:"post",data:e})}function s(e){return Object(i["a"])({url:"/admin/device/test",method:"get",params:e})}function o(e){return Object(i["a"])({url:"/admin/device/delete",method:"DELETE",params:e})}function c(e){return Object(i["a"])({url:"/admin/single/segment",method:"get",params:e})}function l(e){return Object(i["a"])({url:"/admin/single/segment",method:"post",data:e})}function u(e){return Object(i["a"])({url:"/admin/single/segment",method:"DELETE",params:e})}},f890:function(e,t,n){"use strict";n("3e8d")}}]);