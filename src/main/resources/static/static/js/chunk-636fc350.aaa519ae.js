(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-636fc350","chunk-26a5281a"],{"24b4":function(e,t,n){"use strict";n.d(t,"d",(function(){return i})),n.d(t,"e",(function(){return a})),n.d(t,"f",(function(){return r})),n.d(t,"a",(function(){return o})),n.d(t,"b",(function(){return c})),n.d(t,"c",(function(){return l}));var s=n("b775");function i(e){return Object(s["a"])({url:"/admin/gather/type",method:"get",params:e})}function a(e){return Object(s["a"])({url:"/admin/gather/main",method:"get",params:e})}function r(e){return Object(s["a"])({url:"/admin/gather/stop",method:"post",data:e})}function o(e){return Object(s["a"])({url:"/admin/sureying/log",method:"get",params:e})}function c(e){return Object(s["a"])({url:"/admin/sureying/log/single/segment",method:"get",params:e})}function l(e){return Object(s["a"])({url:"/admin/gather/log/status",method:"get",params:e})}},"3bb4":function(e,t,n){"use strict";n.r(t);var s=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-card",{scopedSlots:e._u([{key:"header",fn:function(){return[n("div",{staticClass:"clearfix"},[n("span",{staticStyle:{"line-height":"28px"}},[e._v("测绘日志")]),n("div",{staticClass:"fr"},[n("el-button",{attrs:{size:"small",type:"primary",disabled:e.isRunning},on:{click:e.handBtn}},[e.isRunning?n("span",[n("i",{staticClass:"el-icon-loading"}),e._v(" 测绘中...")]):n("span",[e._v("开始测绘")])]),n("el-button",{directives:[{name:"show",rawName:"v-show",value:!e.noTestRow,expression:"!noTestRow"}],attrs:{size:"small",type:"success"},on:{click:function(t){e.viewDialogFlag=!0}}},[e._v("测绘结果")])],1)])]},proxy:!0}])},[n("div",{directives:[{name:"loading",rawName:"v-loading",value:e.startLoading,expression:"startLoading"}],staticClass:"logWrap fz12"},[e._l(e.logList,(function(t){return[n("div",{key:t.id,staticClass:"logItem"},[n("div",{staticClass:"flexBox"},[4==t.type?[n("div",{staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("p",[e._v(e._s(t.name))])])])]:n("div",{staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("p",[e._v(e._s(t.name))]),n("div",{staticClass:"time"},[n("span",[e._v(e._s(t.beginTime))]),e._v(" ~ "),n("span",[n("span",{directives:[{name:"show",rawName:"v-show",value:1===t.status,expression:"item.status === 1"}],staticClass:"successC"},[n("i",{staticClass:"el-icon-loading"}),n("span",{staticClass:"fz12"},[e._v(e._s("数据上传"===t.name?"数据上传中...":"分析中..."))])]),e._v(" "+e._s(t.endTime)+" ")])])]),n("div",{directives:[{name:"show",rawName:"v-show",value:2===t.status,expression:"item.status === 2"}],staticClass:"dot green"},[e._v("成功")]),n("div",{directives:[{name:"show",rawName:"v-show",value:3===t.status,expression:"item.status === 3"}],staticClass:"dot red"},[e._v("失败")])]),n("div",{staticClass:"actions"})],2),t.subLogs?n("div",{staticClass:"subLogWrap p-l-10"},e._l(t.subLogs,(function(t){return n("div",{key:t.id,staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("p",{staticStyle:{color:"#7b7b7bbf","text-align":"center"}},[e._v(e._s(t.name))]),n("div",{staticClass:"time"},[n("span",[e._v(e._s(t.beginTime))]),e._v(" ~ "),n("span",[n("span",{directives:[{name:"show",rawName:"v-show",value:1===t.status,expression:"subItem.status === 1"}],staticClass:"successC"},[n("i",{staticClass:"el-icon-loading"}),n("span",{staticClass:"fz12"},[e._v(e._s("数据上传"===t.name?"数据上传中...":"分析中..."))])]),e._v(" "+e._s(t.endTime)+" ")])])]),n("div",{directives:[{name:"show",rawName:"v-show",value:2===t.status,expression:"subItem.status === 2"}],staticClass:"dot green"},[e._v("成功")]),n("div",{directives:[{name:"show",rawName:"v-show",value:3===t.status,expression:"subItem.status === 3"}],staticClass:"dot red"},[e._v("失败")])])})),0):e._e()])]})),n("div",{directives:[{name:"show",rawName:"v-show",value:!e.isRunning&&e.logList.length,expression:"!isRunning && logList.length"}],staticClass:"logItem flexBox"},[n("div",{staticClass:"logContent"},[n("div",{staticClass:"logMsg"},[n("p",[e._v("测绘完成")])])])])],2),n("el-dialog",{attrs:{"close-on-click-modal":!1,title:"测绘结果","custom-class":"mw600 smTank",visible:e.viewDialogFlag,width:"80%",top:"20%"},on:{"update:visible":function(t){e.viewDialogFlag=t},open:e.getAsyncList}},[n("div",{directives:[{name:"loading",rawName:"v-loading",value:e.detailIsLoading,expression:"detailIsLoading"}]},[n("fieldset",{staticClass:"m-b-10"},[n("legend",[e._v("终端统计")]),n("el-descriptions",{staticClass:"fz12",attrs:{column:1}},[n("el-descriptions-item",{attrs:{label:"IPv4终端"}},[e._v(e._s(e.rowDetail.ipv4))]),n("el-descriptions-item",{attrs:{label:"IPv4/IPv6终端"}},[e._v(e._s(e.rowDetail.ipv4_ipv6))])],1)],1)])])],1)},i=[],a=n("c7eb"),r=n("1da1"),o=(n("4de4"),n("d3b7"),n("24b4")),c=n("ae6f"),l={props:{configList:{type:Array,default:function(){return[]}}},data:function(){return{noTestRow:!1,viewTablePost:{currentPage:1,pageSize:15},isRunning:!1,startLoading:!1,logTimer:null,logList:[],viewDialogFlag:!1,defaultProps:{children:"subnetList",label:"ip"},detailIsLoading:!1,rowDetail:{result:"",ipv4:"",ipv4_ipv6:"",subme_ipv4:[],subme_ipv6:[],gatewayInfo:[]}}},watch:{configList:{handler:function(e){var t=this.configList.filter((function(e){return 1==e.deviceVendorSequence}));this.noTestRow=t.length>0,console.log(this.noTestRow)},deep:!0}},mounted:function(){this.getCaiJiStatus(),this.getAsyncList()},methods:{getCaiJiStatus:function(){var e=this;return Object(r["a"])(Object(a["a"])().mark((function t(){var n,s;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(o["e"])({type:1});case 2:if(n=t.sent,1002!==n.code){t.next=8;break}e.isRunning=!0,e.getLogList(),t.next=13;break;case 8:return t.next=10,Object(o["a"])();case 10:s=t.sent,200==s.code&&s.data&&(e.logList=s.data.data),e.isRunning=!1;case 13:case"end":return t.stop()}}),t)})))()},stopBtn:function(){var e=this;return Object(r["a"])(Object(a["a"])().mark((function t(){var n;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(o["f"])();case 2:n=t.sent,200===n.code?(e.logTimer&&clearTimeout(e.logTimer),e.getCaiJiStatus(),e.$message.success("停止成功")):e.$message.error(n.msg);case 4:case"end":return t.stop()}}),t)})))()},handBtn:function(){var e=this;return Object(r["a"])(Object(a["a"])().mark((function t(){return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(!e.isRunning){t.next=2;break}return t.abrupt("return",e.$message({message:"正在采集中... 请勿重复提交",type:"warning"}));case 2:Object(o["e"])(),setTimeout((function(){e.getCaiJiStatus()}),500);case 4:case"end":return t.stop()}}),t)})))()},getLogList:function(){var e=this;return Object(r["a"])(Object(a["a"])().mark((function t(){var n;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(o["a"])();case 2:if(n=t.sent,200!=n.code||!n.data){t.next=13;break}if(e.logList=n.data.data,!n.data.finish){t.next=10;break}return e.isRunning=!1,t.abrupt("return");case 10:e.isRunning=!0,e.logTimer&&clearTimeout(e.logTimer),e.logTimer=setTimeout((function(){e.getLogList()}),2e3);case 13:e.startLoading=!1;case 14:case"end":return t.stop()}}),t)})))()},getAsyncList:function(){var e=this;return Object(r["a"])(Object(a["a"])().mark((function t(){var n,s,i;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return e.detailIsLoading=!0,t.next=3,Object(c["a"])(e.viewTablePost);case 3:n=t.sent,200==n.code&&n.data&&(s=n.data.obj[0]||{},i=s.details&&"string"===typeof s.details?JSON.parse(s.details):{},e.rowDetail.result=s.result,e.rowDetail.ipv4=i.ipv4,e.rowDetail.ipv4_ipv6=i.ipv4_ipv6,e.rowDetail.subme_ipv4=i.subme_ipv4&&"string"===typeof i.subme_ipv4?JSON.parse(i.subme_ipv4):[],e.rowDetail.subme_ipv6=i.subme_ipv6&&"string"===typeof i.subme_ipv6?JSON.parse(i.subme_ipv6):[],e.rowDetail.gatewayInfo=i.gatewayInfo),e.detailIsLoading=!1;case 6:case"end":return t.stop()}}),t)})))()}}},d=l,u=(n("ddc1"),n("2877")),p=Object(u["a"])(d,s,i,!1,null,"87e36662",null);t["default"]=p.exports},"51e6":function(e,t,n){"use strict";n("d247")},"703a":function(e,t,n){"use strict";n.r(t);var s=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("el-card",{scopedSlots:e._u([{key:"header",fn:function(){return[n("div",{staticClass:"clearfix"},[n("span",{staticStyle:{"line-height":"28px"}},[e._v("采集设备配置")]),n("div",{staticClass:"fr"},[n("el-button",{attrs:{size:"small",type:"primary",icon:"el-icon-plus"},on:{click:e.addBtn}},[e._v("新增设备")]),n("el-button",{attrs:{size:"small",type:"warning",icon:"el-icon-plus"},on:{click:e.addLogBtn}},[e._v("日志采集")]),n("el-button",{attrs:{size:"small",type:"info"},on:{click:function(t){return e.$router.push({path:"/"})}}},[e._v("返回首页")])],1)])]},proxy:!0}])},[n("div",{directives:[{name:"loading",rawName:"v-loading",value:e.isLoading,expression:"isLoading"}],staticClass:"fromWrap"},[n("el-empty",{directives:[{name:"show",rawName:"v-show",value:!e.formArr.length,expression:"!formArr.length"}],attrs:{description:"暂无数据..."}}),e._l(e.formArr,(function(t,s){return n("el-form",{key:s,ref:"formItem"+s,refInFor:!0,staticClass:"formItemEle m-b-10 p-b-10 p-l-10 p-r-10",attrs:{model:t,rules:e.rules,"label-position":"top",size:"mini"}},[n("div",{staticClass:"flex between wrap",staticStyle:{"align-items":"self-end"}},[n("el-col",{staticClass:"inputWrap",attrs:{md:18,lg:20}},[n("el-form-item",{attrs:{label:s+1+"# 设备名称",prop:"name"}},[n("el-input",{model:{value:t.name,callback:function(n){e.$set(t,"name","string"===typeof n?n.trim():n)},expression:"formItem.name"}})],1),n("el-form-item",{staticStyle:{width:"160px"},attrs:{label:"设备类型/品牌",prop:"deviceTypeVendorArr"}},[n("el-cascader",{attrs:{options:e.deviceTypeVendorSelectArr,props:{expandTrigger:"hover",children:"deviceVendorList",label:"name",value:"id"}},on:{change:function(n){return e.handleChangeTypeVendor(s,t)}},model:{value:t.deviceTypeVendorArr,callback:function(n){e.$set(t,"deviceTypeVendorArr",n)},expression:"formItem.deviceTypeVendorArr"}})],1),1!=t.type?[n("el-form-item",{attrs:{label:"管理IP",prop:"ip"}},[n("el-input",{model:{value:t.ip,callback:function(n){e.$set(t,"ip","string"===typeof n?n.trim():n)},expression:"formItem.ip"}})],1),n("el-form-item",{attrs:{label:"登陆方式",prop:"loginType"}},[n("el-select",{on:{change:function(n){return e.loginTypeChange(t)}},model:{value:t.loginType,callback:function(n){e.$set(t,"loginType","string"===typeof n?n.trim():n)},expression:"formItem.loginType"}},[n("el-option",{attrs:{value:"ssh"}},[e._v("ssh")]),n("el-option",{attrs:{value:"telnet"}},[e._v("telnet")]),n("el-option",{attrs:{value:"api"}},[e._v("api")])],1)],1),n("el-form-item",{attrs:{label:"端口"}},[n("el-input",{attrs:{placeholder:"ssh"===t.loginType?"22":"23"},model:{value:t.loginPort,callback:function(n){e.$set(t,"loginPort","string"===typeof n?n.trim():n)},expression:"formItem.loginPort"}})],1),n("el-form-item",{attrs:{label:"用户名",prop:"loginName"}},[n("el-input",{attrs:{autocomplete:"new-login"},model:{value:t.loginName,callback:function(n){e.$set(t,"loginName","string"===typeof n?n.trim():n)},expression:"formItem.loginName"}})],1),n("el-form-item",{attrs:{label:"密码",prop:"loginPassword"}},[n("el-input",{attrs:{type:"password",autocomplete:"new-password"},model:{value:t.loginPassword,callback:function(n){e.$set(t,"loginPassword","string"===typeof n?n.trim():n)},expression:"formItem.loginPassword"}})],1),"派网"==t.deviceVendorName?n("el-form-item",{staticStyle:{width:"150px"},attrs:{label:"双栈模式"}},[n("el-select",{attrs:{placeholder:"请选择"},model:{value:t.state,callback:function(n){e.$set(t,"state",n)},expression:"formItem.state"}},[n("el-option",{attrs:{label:"IPv4网关在派网",value:!0}}),n("el-option",{attrs:{label:"IPv4网关不在派网",value:!1}})],1)],1):e._e()]:e._e()],2),1!=t.type?[n("div",[n("p",{staticClass:"textTips fz14",staticStyle:{"margin-bottom":"4px"}},[1===t.testFlag?n("span",{staticClass:"successC"},[e._v("登录正常")]):e._e(),0===t.testFlag?n("span",{staticClass:"dangerC"},[e._v("登录失败")]):e._e()]),n("div",[n("el-button",{directives:[{name:"show",rawName:"v-show",value:0==t.deviceVendorSequence,expression:"formItem.deviceVendorSequence == 0"}],attrs:{plain:"",size:"mini",type:"primary",disabled:t.testLoading},on:{click:function(n){return e.testBtn(t,s)}}},[n("i",{directives:[{name:"show",rawName:"v-show",value:t.testLoading,expression:"formItem.testLoading"}],staticClass:"el-icon-loading"}),e._v(" 测试")]),n("el-button",{attrs:{plain:"",size:"mini",type:"danger"},on:{click:function(n){return e.delRow(t,s)}}},[e._v("删除")])],1)])]:e._e()],2)])}))],2),n("div",{directives:[{name:"show",rawName:"v-show",value:e.formArr.length,expression:"formArr.length"}],staticClass:"flexCC"},[n("el-button",{attrs:{type:"success"},on:{click:e.saveBtn}},[e._v("保存配置")])],1)]),n("hand",{staticClass:"m-t-10",attrs:{configList:e.formArr}})],1)},i=[],a=n("c7eb"),r=n("1da1"),o=n("3835"),c=(n("7db0"),n("d3b7"),n("b0c0"),n("d81d"),n("e9c4"),n("159b"),n("a9e3"),n("a434"),n("da71")),l=n("3bb4"),d={components:{hand:l["default"]},data:function(){return{isLoading:!1,formArr:[{name:"",type:0,deviceTypeVendorArr:[],deviceTypeId:"",deviceVendorId:"",deviceVendorSequence:"",deviceModelId:"",ip:"",loginType:"ssh",loginPort:"",loginName:"",loginPassword:"",testLoading:!1,testFlag:null,state:null}],rules:{name:[{required:!0,message:"请输入设备名称",trigger:"blur"}],deviceTypeVendorArr:[{required:!0,message:"请输入设备类型、品牌",trigger:"blur"}],deviceVendorId:[{required:!0,message:"请输入品牌",trigger:"blur"}],ip:[{required:!0,message:"请输入管理IP",trigger:"blur"}],loginType:[{required:!0,message:"请选择登陆方式",trigger:"blur"}],loginName:[{required:!0,message:"请输入用户名",trigger:"blur"}],loginPassword:[{required:!0,message:"请输入密码",trigger:"blur"}]},deviceTypeVendorSelectArr:[],deviceVendorArr:[]}},created:function(){this.getList()},methods:{addBtn:function(){this.formArr.push({id:"",type:0,name:"",deviceTypeVendorArr:[],deviceTypeId:"",deviceVendorId:"",deviceVendorSequence:"",deviceModelId:"",ip:"",loginType:"ssh",loginPort:"",loginName:"",loginPassword:"",testLoading:!1,testFlag:null,state:null})},addLogBtn:function(){this.formArr.push({id:"",type:1,name:"日志采集设备",deviceTypeVendorArr:[],deviceTypeId:"",deviceVendorId:"",deviceVendorSequence:"",deviceModelId:"",ip:"",loginType:"ssh",loginPort:"",loginName:"",loginPassword:"",testLoading:!1,testFlag:null,state:null})},handleChangeTypeVendor:function(e,t){console.log(t.deviceTypeVendorArr);var n=Object(o["a"])(t.deviceTypeVendorArr,2),s=n[0],i=n[1];if(s&&i){this.formArr[e].deviceTypeId=s||"",this.formArr[e].deviceVendorId=i||"";var a=this.deviceTypeVendorSelectArr.find((function(e){return e.id==s})),r=a&&a.deviceVendorList.find((function(e){return e.id==i}));this.formArr[e].deviceVendorSequence=r.sequence,this.formArr[e].deviceVendorName=r.name,"派网"==r.name&&(this.formArr[e].state=!0)}},getList:function(){var e=this;return Object(r["a"])(Object(a["a"])().mark((function t(){var n,s,i,r;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return e.isLoading=!0,t.next=3,Object(c["d"])({currentPage:1,pageSize:999});case 3:n=t.sent,200===n.code&&n.data&&(s=n.data,i=s.obj,r=s.other,e.formArr=i.map((function(e){return e.deviceTypeVendorArr=[e.deviceTypeId,e.deviceVendorId],e.testLoading=!1,e.testFlag=null,e})),r&&(e.deviceTypeVendorSelectArr=r.deviceType,e.deviceVendorArr=r.deviceVendor)),e.isLoading=!1;case 6:case"end":return t.stop()}}),t)})))()},loginTypeChange:function(e){var t="";"ssh"===e.loginType?t="22":"telnet"===e.loginType?t="23":"api"===e.loginType&&(t="443"),e.loginPort=t},saveBtn:function(){for(var e=!0,t=0;t<this.formArr.length;t++){var n=this.formArr[t];delete n.createTime,this.$refs["formItem"+t][0].validate(function(){var t=Object(r["a"])(Object(a["a"])().mark((function t(n){return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:n||(e=!1);case 1:case"end":return t.stop()}}),t)})));return function(e){return t.apply(this,arguments)}}())}e&&this.saveFn()},saveFn:function(){var e=this;return Object(r["a"])(Object(a["a"])().mark((function t(){var n,s;return Object(a["a"])().wrap((function(t){while(1)switch(t.prev=t.next){case 0:return n=JSON.parse(JSON.stringify(e.formArr)),n.forEach((function(e){e.loginPort||(e.loginPort="ssh"===e.loginType?"22":"23"),delete e.deviceTypeVendorArr,delete e.testLoading,delete e.testFlag,delete e.deviceVendorSequence})),t.next=4,Object(c["e"])(n);case 4:s=t.sent,200===s.code?(e.$message.success("保存成功"),e.getList()):e.$message.error(s.msg);case 6:case"end":return t.stop()}}),t)})))()},testBtn:function(e,t){var n=this;return Object(r["a"])(Object(a["a"])().mark((function s(){var i;return Object(a["a"])().wrap((function(s){while(1)switch(s.prev=s.next){case 0:if(e.id){s.next=2;break}return s.abrupt("return",n.$message.error("请先保存"));case 2:return e.testLoading=!0,n.formArr[t].testFlag=null,s.next=6,Object(c["g"])({id:e.id});case 6:i=s.sent,200===i.code?n.formArr[t].testFlag=Number(i.data):n.$message.error(i.msg),e.testLoading=!1;case 9:case"end":return s.stop()}}),s)})))()},delRow:function(e,t){var n=this;return Object(r["a"])(Object(a["a"])().mark((function s(){var i;return Object(a["a"])().wrap((function(s){while(1)switch(s.prev=s.next){case 0:if(!e.id){s.next=7;break}return s.next=3,Object(c["a"])({ids:e.id});case 3:i=s.sent,200===i.code?(n.$message.success("删除成功"),n.getList()):n.$message.error(i.msg),s.next=8;break;case 7:n.formArr.splice(t,1);case 8:case"end":return s.stop()}}),s)})))()}}},u=d,p=(n("51e6"),n("2877")),m=Object(p["a"])(u,s,i,!1,null,"ce868c3c",null);t["default"]=m.exports},"7db0":function(e,t,n){"use strict";var s=n("23e7"),i=n("b727").find,a=n("44d2"),r=n("ae40"),o="find",c=!0,l=r(o);o in[]&&Array(1)[o]((function(){c=!1})),s({target:"Array",proto:!0,forced:c||!l},{find:function(e){return i(this,e,arguments.length>1?arguments[1]:void 0)}}),a(o)},ae6f:function(e,t,n){"use strict";n.d(t,"a",(function(){return i}));var s=n("b775");function i(e){return Object(s["a"])({url:"/admin/gather/log/list",method:"post",data:e})}},d06c:function(e,t,n){},d247:function(e,t,n){},da71:function(e,t,n){"use strict";n.d(t,"d",(function(){return i})),n.d(t,"e",(function(){return a})),n.d(t,"g",(function(){return r})),n.d(t,"a",(function(){return o})),n.d(t,"c",(function(){return c})),n.d(t,"f",(function(){return l})),n.d(t,"b",(function(){return d}));var s=n("b775");function i(e){return Object(s["a"])({url:"/admin/device/list",method:"post",data:e})}function a(e){return Object(s["a"])({url:"/admin/device/batch/save",method:"post",data:e})}function r(e){return Object(s["a"])({url:"/admin/device/test",method:"get",params:e})}function o(e){return Object(s["a"])({url:"/admin/device/delete",method:"DELETE",params:e})}function c(e){return Object(s["a"])({url:"/admin/single/segment",method:"get",params:e})}function l(e){return Object(s["a"])({url:"/admin/single/segment",method:"post",data:e})}function d(e){return Object(s["a"])({url:"/admin/single/segment",method:"DELETE",params:e})}},ddc1:function(e,t,n){"use strict";n("d06c")}}]);