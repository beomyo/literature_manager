"use strict";(self["webpackChunkvue"]=self["webpackChunkvue"]||[]).push([[697],{697:function(e,t,s){s.r(t),s.d(t,{default:function(){return o}});var i=function(){var e=this,t=e._self._c;return t("div",[t("div",{staticClass:"card",staticStyle:{padding:"15px"}},[e._v(" 您好，"+e._s(e.user?.name)+"！欢迎使用本系统 ")]),t("div",{staticStyle:{display:"flex",margin:"10px 0"}},[t("div",{staticClass:"card",staticStyle:{width:"50%"}},[t("div",{staticStyle:{"margin-bottom":"30px","font-size":"20px","font-weight":"bold"}},[e._v("公告列表")]),t("div",[t("el-timeline",{attrs:{slot:"reference",reverse:""},slot:"reference"},e._l(e.notices,(function(s){return t("el-timeline-item",{key:s.id,attrs:{timestamp:s.time}},[t("el-popover",{attrs:{placement:"right",width:"200",trigger:"hover",content:s.content}},[t("span",{attrs:{slot:"reference"},slot:"reference"},[e._v(e._s(s.title))])])],1)})),1)],1)])])])},r=[],n={name:"Home",data(){return{user:JSON.parse(localStorage.getItem("xm-user")||"{}"),notices:[]}},created(){this.$request.get("/notice/selectAll").then((e=>{this.notices=e.data||[]}))}},a=n,l=s(1656),c=(0,l.A)(a,i,r,!1,null,null,null),o=c.exports}}]);
//# sourceMappingURL=697.69e0b6a3.js.map