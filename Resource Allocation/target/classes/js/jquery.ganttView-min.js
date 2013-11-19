(function(f){f.fn.ganttView=function(){var i=Array.prototype.slice.call(arguments);if(i.length==1&&typeof(i[0])=="object"){h.call(this,i[0])}if(i.length==2&&typeof(i[0])=="string"){c.call(this,i[0],i[1])}};function h(i){var j=this;var l={showWeekends:true,cellWidth:31,cellHeight:31,slideWidth:400,vHeaderWidth:100,behavior:{clickable:true,draggable:true,resizable:true}};var k=f.extend(true,l,i);if(k.data){m()}else{if(k.dataUrl){f.getJSON(k.dataUrl,function(n){k.data=n;m()})}}function m(){var n=Math.floor((k.slideWidth/k.cellWidth)+5);var o=g.getBoundaryDatesFromData(k.data,n);k.start=o[0];k.end=o[1];console.log("start: "+k.start);console.log("end: "+k.end);j.each(function(){var r=f(this);var u=f("<div>",{"class":"ganttview"});var q=new a(u,k).render();r.append(u);var p=f("div.ganttview-vtheader",r).outerWidth()+f("div.ganttview-slide-container",r).outerWidth();r.css("width",(p+2)+"px");for(var s=0;s<q[0].length;s++){var t=document.getElementById(q[0][s]);t.className=t.className+" fpt-gantt-color-cell-red"}for(var s=0;s<q[1].length;s++){var t=document.getElementById(q[1][s]);t.className=t.className+" fpt-gantt-color-cell-green"}for(var s=0;s<q[2].length;s++){var t=document.getElementById(q[2][s]);t.className=t.className+" fpt-gantt-color-cell-yellow"}new e(r,k).apply()});b(k.start,k.end)}}function b(l){var k=$("#id-ganttview-slide-container").width();var j=g.daysBetween(l,new Date());var i=Math.floor(k/31);if(i<j){$("#id-ganttview-slide-container").scrollLeft((j-2)*30)}}function c(k,i){if(k=="setSlideWidth"){var j=$("div.ganttview",this);j.each(function(){var l=$("div.ganttview-vtheader",j).outerWidth();$(j).width(l+i+1);$("div.ganttview-slide-container",this).width(i)})}}var a=function(k,j){function l(){m(k,j.data,j.cellHeight);var y=f("<div>",{id:"id-ganttview-slide-container","class":"ganttview-slide-container",css:{width:j.slideWidth+"px"}});dates=q(j.start,j.end);u(y,dates,j.cellWidth);w(y,j.data,dates,j.cellWidth,j.showWeekends);r(y,j.data);var z=o(y,j.data,j.cellWidth,j.start);k.append(y);s(k.parent());return z}var p=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];function q(C,y){var B=[];B[C.getFullYear()]=[];B[C.getFullYear()][C.getMonth()]=[C];var A=C;while(A.compareTo(y)==-1){var z=A.clone().addDays(1);if(!B[z.getFullYear()]){B[z.getFullYear()]=[]}if(!B[z.getFullYear()][z.getMonth()]){B[z.getFullYear()][z.getMonth()]=[]}B[z.getFullYear()][z.getMonth()].push(z);A=z}return B}function m(z,F,M){var J=f("<div>",{"class":"ganttview-vtheader"});for(var G=0;G<F.length;G++){var C=f("<div>",{"class":"ganttview-vtheader-item"});C.append(f("<div>",{id:"fpt-gantt_p-"+G,"class":"ganttview-vtheader-item-name",css:{height:"31px",width:"315px",background:"#ddd"}}).append(F[G].name));var A=f("<div>",{id:"fpt-gantt_root_u_p-"+G,"class":"ganttview-vtheader-series"});for(var D=0;D<F[G].user.length;D++){var L=f("<div>",{id:"fpt-gantt_root_i_u-"+D+"_p-"+G,style:"float:left;background:#ddd"});L.append(f("<div>",{id:"fpt-gantt_u-"+D+"_p-"+G,"class":"ganttview-vtheader-item-name",css:{height:"31px",width:"147px","padding-left":"18px",background:"#ddd"}}).append(F[G].user[D].name));var N=f("<div>",{id:"fpt-gantt_root_block_i_u-"+D+"_p-"+G,"class":"ganttview-vtheader-series"});var I=false;for(var E=0;E<F[G].user[D].series.length;E++){var K=f("<div>",{id:"fpt-gantt_i-"+E+"_u-"+D+"_p-"+G,"class":"ganttview-vtheader-series-name"});if(!I&&F[G].user[D].series[E].name=="Planned"){N.append(K.append(f("<p>",{css:{"margin-top":"7px"}}).append(F[G].user[D].series[E].name)));I=true}else{if(F[G].user[D].series[E].name!="Planned"){if(F[G].user[D].series[E].name=="Project Usage"){K.attr("id","fpt-gantt_root_a_u-"+D+"_p-"+G);K.removeClass("ganttview-vtheader-series-name");K.append(f("<div>",{id:"fpt-gantt_a_u-"+D+"_p-"+G,"class":"ganttview-vtheader-series-name"}).append(f("<div>",{css:{"margin-top":"7px"},id:"fpt-gantt_a_u-"+D+"_p-"+G+"_u-"+D}).append(F[G].user[D].series[E].name)));var H=f("<div>",{id:"fpt-gantt_root_block_a_u-"+D+"_p-"+G});var y=F[G].user[D].series[E].issues;for(var B=0;B<y.length;B++){if(y[B].name!=""){H.append(f("<div>",{title:F[G].user[D].series[E].issues[B].name,id:"fpt-gantt_a-"+B+"_u-"+D+"_p-"+G,"class":"ganttview-vtheader-series-name"}).append(f("<p>",{css:{"margin-left":"30px","margin-top":"7px"}}).append(f("<a>",{href:"/browse/"+F[G].user[D].series[E].issues[B].key}).append("["+F[G].user[D].series[E].issues[B].key+"]"))))}}K.append(H);N.append(K)}else{N.append(K.append(F[G].user[D].series[E].name))}}}}L.append(N);A.append(L)}C.append(A);J.append(C)}z.append(J)}function u(z,A,C){var H=f("<div>",{"class":"ganttview-hzheader"});var F=f("<div>",{"class":"ganttview-hzheader-months"});var E=f("<div>",{"class":"ganttview-hzheader-days"});var D=0;for(var I in A){for(var B in A[I]){var J=A[I][B].length*C;D=D+J;F.append(f("<div>",{"class":"ganttview-hzheader-month",css:{width:(J-1)+"px"}}).append(p[B]+"/"+I));for(var G in A[I][B]){E.append(f("<div>",{"class":"ganttview-hzheader-day"}).append(A[I][B][G].getDate()))}}}F.css("width",D+"px");E.css("width",D+"px");H.append(F).append(E);z.append(H)}function x(A,z,B,E){var F=f("<div>",{"class":"ganttview-grid-row"});for(var H in z){for(var C in z[H]){for(var G in z[H][C]){var D=f("<div>",{"class":"ganttview-grid-row-cell",id:"r-"+A+"_y-"+H+"_m-"+C+"_d-"+z[H][C][G].getDate()});if(g.isWeekend(z[H][C][G])&&E){D.addClass("ganttview-weekend")}F.append(D)}}}var I=f("div.ganttview-grid-row-cell",F).length*B;F.css("width",I+"px");return F}function w(J,T,B,P,L){var E=f("<div>",{"class":"ganttview-grid"});var H=0;var z=x(H,B,P,L);var I=f("div.ganttview-grid-row-cell",z).length*P;E.css("width",I+"px");for(var R=0;R<T.length;R++){var D=f("<div>",{id:"fpt-gantt_root_rp-"+R});var y=x(H,B,P,L);D.append(y.attr("id","fpt-gantt_rp-"+R));H=H+1;var F=f("<div>",{id:"fpt-gantt_root_ru_rp-"+R});for(var O=0;O<T[R].user.length;O++){var C=f("<div>",{id:"fpt-gantt_root_ri_ru-"+O+"_rp-"+R});var Q=false;for(var N=0;N<T[R].user[O].series.length;N++){if(!Q&&T[R].user[O].series[N].name=="Planned"){var K=x(H,B,P,L);C.append(K.attr("id","fpt-gantt_ri-"+N+"_ru-"+O+"_rp-"+R));H=H+1;Q=true}else{if(T[R].user[O].series[N].name!="Planned"){if(T[R].user[O].series[N].name=="Project Usage"){var G=f("<div>",{id:"fpt-gantt_root_ra_ru-"+O+"_rp-"+R});var K=x(H,B,P,L);G.append(K.attr("id","fpt-gantt_ra_ru-"+O+"_rp-"+R));H=H+1;var S=f("<div>",{id:"fpt-gantt_root_block_ra_ru-"+O+"_rp-"+R});for(var M=0;M<T[R].user[O].series[N].issues.length;M++){if(T[R].user[O].series[N].issues[M].name!=""){var A=x(H,B,P,L);S.append(A.attr("id","fpt-gantt_ra-"+M+"_ru-"+O+"_rp-"+R));H=H+1}}G.append(S);C.append(G)}else{var K=x(H,B,P,L);C.append(K.attr("id","fpt-gantt_ri-"+N+"_ru-"+O+"_rp-"+R));H=H+1}}}}F.append(C)}D.append(F);E.append(D)}J.append(E)}function r(y,F){var G=f("<div>",{"class":"ganttview-blocks"});for(var H=0;H<F.length;H++){var B=f("<div>",{id:"fpt-gantt_root_bp-"+H});B.append(f("<div>",{id:"fpt-gantt_bp-"+H,"class":"ganttview-block-container"}));var D=f("<div>",{id:"fpt-gantt_root_bu_bp-"+H});for(var E=0;E<F[H].user.length;E++){var K=f("<div>",{id:"fpt-gantt_root_bi_bu-"+E+"_bp-"+H});var J=false;for(var C=0;C<F[H].user[E].series.length;C++){if(!J&&F[H].user[E].series[C].name=="Planned"){K.append(f("<div>",{id:"fpt-gantt_bi-"+C+"_bu-"+E+"_bp-"+H,"class":"ganttview-block-container"}));J=true}else{if(F[H].user[E].series[C].name!="Planned"){if(F[H].user[E].series[C].name=="Project Usage"){var I=f("<div>",{id:"fpt-gantt_root_ba_bu-"+E+"_bp-"+H});I.append(f("<div>",{id:"fpt-gantt_ba_bu-"+E+"_bp-"+H,"class":"ganttview-block-container"}));var A=f("<div>",{id:"fpt-gantt_root_block_ba_bu-"+E+"_bp-"+H});for(var z=0;z<F[H].user[E].series[C].issues.length;z++){if(F[H].user[E].series[C].issues[z].name!=""){A.append(f("<div>",{id:"fpt-gantt_ba-"+z+"_bu-"+E+"_bp-"+H,"class":"ganttview-block-container"}))}}I.append(A);K.append(I)}else{K.append(f("<div>",{id:"fpt-gantt_bi-"+C+"_bu-"+E+"_bp-"+H,"class":"ganttview-block-container"}))}}}}D.append(K)}B.append(D);G.append(B)}y.append(G)}function o(ae,ag,T,O){O=new Date(O.getFullYear(),O.getMonth(),O.getDate(),0,0,0);var ac=f("div.ganttview-blocks div.ganttview-block-container",ae);var ah=0;var H=[];var Z=[];var A=[];for(var ab=0;ab<ag.length;ab++){ah=ah+1;var K=[];var z=[];var ad=0;for(var aa=0;aa<ag[ab].user.length;aa++){ah=ah+1;ad=ad+1;var I=[];var Q=false;for(var X=0;X<ag[ab].user[aa].series.length;X++){var C=ag[ab].user[aa].series[X];C.start=new Date(C.start.getFullYear(),C.start.getMonth(),C.start.getDate(),0,0,0);C.end=new Date(C.end.getFullYear(),C.end.getMonth(),C.end.getDate(),0,0,0);var N=g.daysBetween(O,C.start);var B=g.daysBetween(C.start,C.end)+1;var y=g.daysBetween(O,C.end)+1;var E=C.name!="Planned"?v(parseFloat(C.totalHour)/(B*3600)):C.totalHour/3600;if(C.name=="Planned"){var R=[];for(var V=0;V<y;V++){if(V<N){R[R.length]=0}else{R[R.length]=E}}I[I.length]=R;if(!Q){ah=ah+1;ad=ad+1;Q=true}}var D=[];if(C.name=="Project Usage"){for(var V=0;V<C.issues.length;V++){ah=ah+1;ad=ad+1;var af=C.issues[V];af.start=new Date(C.start.getFullYear(),af.start.getMonth(),af.start.getDate(),0,0,0);af.end=new Date(C.end.getFullYear(),af.end.getMonth(),af.end.getDate(),0,0,0);var N=g.daysBetween(O,af.start);var y=g.daysBetween(O,af.end)+1;var B=g.daysBetween(af.start,af.end)+1;var M=g.sizeWorkingDays(af.start,af.end);E=v(parseFloat(af.totalHour)/(M*3600));if(C.name!=""){var Y=[];for(var U=0;U<y;U++){if(U<N){Y[Y.length]=0}else{Y[Y.length]=E}}D[D.length]=Y}if(V!=0){var J=af.start;for(var U=0;U<B;U++){var S=g.daysBetween(O,J);var F=f("<div>",{"class":"ganttview-block",title:af.name+", "+1+" days",css:{width:((1*T)-9)+"px","margin-left":((S*T)+3)+"px"}});if(!g.isWeekend(J)){F.append(f("<div>",{"class":"ganttview-block-text"}).text(E));f(ac[ah-1]).append(F)}J=new Date(J.getFullYear(),J.getMonth(),J.getDate()+1,J.getHours(),J.getMinutes(),J.getSeconds())}}}var Y=n(D);for(var V=0;V<Y.length;V++){var J=new Date(O.getFullYear(),O.getMonth(),O.getDate()+V);var S=g.daysBetween(O,J);var F=f("<div>",{"class":"ganttview-block",title:C.name+", "+1+" days",css:{width:((1*T)-9)+"px","margin-left":((S*T)+3)+"px"}});if(Y[V]!=0){F.append(f("<div>",{"class":"ganttview-block-text",css:{color:"black"}}).text(Y[V]));if(!g.isWeekend(J)){f(ac[ah-C.issues.length]).append(F);var G=I[I.length-1];G[V]=typeof G[V]=="undefined"?0:G[V];if(parseFloat(Y[V])>parseFloat(G[V])){H[H.length]="r-"+(ah-C.issues.length)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{if(parseFloat(Y[V])==parseFloat(G[V])){Z[Z.length]="r-"+(ah-C.issues.length)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{A[A.length]="r-"+(ah-C.issues.length)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}}}}J=new Date(J.getFullYear(),J.getMonth(),J.getDate()+1,J.getHours(),J.getMinutes(),J.getSeconds())}z[z.length]=Y}}var R=n(I);for(var V=0;V<R.length;V++){var J=new Date(O.getFullYear(),O.getMonth(),O.getDate()+V);var S=g.daysBetween(O,J);var F=f("<div>",{"class":"ganttview-block",title:C.name+", "+1+" days",css:{width:((1*T)-9)+"px","margin-left":((S*T)+3)+"px"}});if(R[V]!=0){if(!g.isWeekend(J)){var D=ag[ab].user[aa].series[ag[ab].user[aa].series.length-1];F.append(f("<div>",{"class":"ganttview-block-text",css:{color:"black"}}).text(R[V]));f(ac[ah-D.issues.length-1]).append(F);if(parseFloat(R[V])>8){H[H.length]="r-"+(ah-D.issues.length-1)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{if(parseFloat(R[V])==8){Z[Z.length]="r-"+(ah-D.issues.length-1)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{A[A.length]="r-"+(ah-D.issues.length-1)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}}}}}K[K.length]=R}if(ag[ab].type=="user"){var L=n(K);for(var aa=0;aa<L.length;aa++){var J=new Date(O.getFullYear(),O.getMonth(),O.getDate()+aa);var S=g.daysBetween(O,J);var F=f("<div>",{"class":"ganttview-block",title:1+" days",css:{width:((1*T)-9)+"px","margin-left":((S*T)+3)+"px"}});if(L[aa]!=0){if(!g.isWeekend(J)){F.append(f("<div>",{"class":"ganttview-block-text",css:{color:"black"}}).text(L[aa]));f(ac[ah-ad]).append(F);if(parseFloat(L[aa])>8){H[H.length]="r-"+(ah-ad)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{if(parseFloat(L[aa])==8){Z[Z.length]="r-"+(ah-ad)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{A[A.length]="r-"+(ah-ad)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}}}}}var W=n(z);for(var aa=0;aa<W.length;aa++){var J=new Date(O.getFullYear(),O.getMonth(),O.getDate()+aa);var S=g.daysBetween(O,J);var F=f("<div>",{"class":"ganttview-block",title:1+" days",css:{width:((1*T)-9)+"px","margin-left":((S*T)+3)+"px"}});if(W[aa]!=0){if(!g.isWeekend(J)){F.append(f("<div>",{"class":"ganttview-block-text",css:{color:"black"}}).text(W[aa]));f(ac[ah-ad+1]).append(F);L[aa]=typeof L[aa]=="undefined"?0:L[aa];if(parseFloat(W[aa])>parseFloat(L[aa])){H[H.length]="r-"+(ah-ad+1)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{if(parseFloat(W[aa])==parseFloat(L[aa])){Z[Z.length]="r-"+(ah-ad+1)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}else{A[A.length]="r-"+(ah-ad+1)+"_y-"+J.getFullYear()+"_m-"+J.getMonth()+"_d-"+J.getDate()}}}}}}}var P=[H,Z,A];return P}function n(C){var D=[];var B=0;for(var z=0;z<C.length;z++){if(B<=C[z].length){B=C[z].length}}for(var z=0;z<B;z++){var A=0;for(var y=0;y<C.length;y++){if(typeof(C[y][z])=="undefined"){A=A+0}else{A=A+C[y][z]}}D[z]=v(A)}return D}function t(y){while(1==1){}}function v(y){return Math.round(y*10)/10}function i(B,A,y){var z={id:A.id,name:A.name};f.extend(z,y);B.data("block-data",z)}function s(y){f("div.ganttview-grid-row div.ganttview-grid-row-cell:last-child",y).addClass("last");f("div.ganttview-hzheader-days div.ganttview-hzheader-day:last-child",y).addClass("last");f("div.ganttview-hzheader-months div.ganttview-hzheader-month:last-child",y).addClass("last")}return{render:l,array:""}};var e=function(o,m){function j(){}function n(q,p){}function l(s,q,p,r){}function k(s,q,p,r){}function i(s,r,q,p){}return{apply:j}};var d={contains:function(j,m){var k=false;for(var l=0;l<j.length;l++){if(j[l]==m){k=true}}return k}};var g={daysBetween:function(l,i){if(!l||!i){return 0}l=Date.parse(l);i=Date.parse(i);if(l.getYear()==1901||i.getYear()==8099){return 0}var k=0,j=l.clone();while(j.compareTo(i)==-1){k=k+1;j.addDays(1)}return k},sizeWorkingDays:function(l,i){if(!l||!i){return 0}l=Date.parse(l);i=Date.parse(i);if(l.getYear()==1901||i.getYear()==8099){return 0}var k=0,j=l.clone();i.addDays(1);while(j.compareTo(i)==-1){if(!g.isWeekend(j)){k=k+1}j.addDays(1)}return k},isWeekend:function(i){return i.getDay()%6==0},getBoundaryDatesFromData:function(v,m){var o=new Date();var p=new Date();for(var w=0;w<v.length;w++){for(var u=0;u<v[w].user.length;u++){for(var t=0;t<v[w].user[u].series.length;t++){var q=Date.parse(v[w].user[u].series[t].start);var s=Date.parse(v[w].user[u].series[t].end);if(w==0&&u==0&&t==0){o=q;p=s}if(o.compareTo(q)==1){o=q}if(p.compareTo(s)==-1){p=s}if(v[w].user[u].series[t].name=="Project Usage"){for(var r=0;r<v[w].user[u].series[t].issues.length;r++){var n=Date.parse(v[w].user[u].series[t].issues[r].start);var x=Date.parse(v[w].user[u].series[t].issues[r].end);if(w==0&&u==0&&t==0&&r==0){o=q;p=s}if(o.compareTo(n)==1){o=n}if(p.compareTo(x)==-1){p=x}}}}}}if(g.daysBetween(o,p)<m){p=o.clone().addDays(m)}return[o,p]}}})(jQuery);