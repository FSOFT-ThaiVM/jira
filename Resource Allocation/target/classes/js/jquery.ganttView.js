/*
jQuery.ganttView v.0.8.8
Copyright (c) 2010 JC Grubbs - jc.grubbs@devmynd.com
MIT License Applies
*/

/*
Options
-----------------
showWeekends: boolean
data: object
cellWidth: number
cellHeight: number
slideWidth: number
dataUrl: string
behavior: {
	clickable: boolean,
	draggable: boolean,
	resizable: boolean,
	onClick: function,
	onDrag: function,
	onResize: function
}
*/

(function (jQuery) {
	
    jQuery.fn.ganttView = function () {
    	
    	var args = Array.prototype.slice.call(arguments);
    	if (args.length == 1 && typeof(args[0]) == "object") {
        	build.call(this, args[0]);
    	}
    	if (args.length == 2 && typeof(args[0]) == "string") {
    		handleMethod.call(this, args[0], args[1]);
    	}
    };
    
    function build(options) {
    	
    	var els = this;
        var defaults = {
            showWeekends: true,
            cellWidth: 31,
            cellHeight: 31,
            slideWidth: 400,
            vHeaderWidth: 100,
            behavior: {
            	clickable: true,
            	draggable: true,
            	resizable: true
            }
        };
        
        var opts = jQuery.extend(true, defaults, options);

		if (opts.data) {
			build();
		} else if (opts.dataUrl) {
			jQuery.getJSON(opts.dataUrl, function (data) { opts.data = data; build(); });
		}

		function build() {
			
			var minDays = Math.floor((opts.slideWidth / opts.cellWidth)  + 5);
			var startEnd = DateUtils.getBoundaryDatesFromData(opts.data, minDays);

			opts.start = startEnd[0];
			opts.end = startEnd[1];

            console.log("start: " + opts.start);
            console.log("end: " + opts.end);
	        els.each(function () {

	            var container = jQuery(this);
	            var div = jQuery("<div>", { "class": "ganttview" });
	            var arrayColor = new Chart(div, opts).render();
				container.append(div);
				
				var w = jQuery("div.ganttview-vtheader", container).outerWidth() +
					jQuery("div.ganttview-slide-container", container).outerWidth();
	            container.css("width", (w + 2) + "px");
                for(var i = 0; i < arrayColor[0].length; i++) {
                    var d = document.getElementById(arrayColor[0][i]);
                    d.className = d.className + " fpt-gantt-color-cell-red";
                }
                for(var i = 0; i < arrayColor[1].length; i++) {
                    var d = document.getElementById(arrayColor[1][i]);
                    d.className = d.className + " fpt-gantt-color-cell-green";
                }
                for(var i = 0; i < arrayColor[2].length; i++) {
                    var d = document.getElementById(arrayColor[2][i]);
                    d.className = d.className + " fpt-gantt-color-cell-yellow";
                }
	            new Behavior(container, opts).apply();
	        });

            scrollToCurrentPosition(opts.start, opts.end);
		}
    }

    function scrollToCurrentPosition(start) {
        var widthContainer = $("#id-ganttview-slide-container").width();
        var size = DateUtils.daysBetween(start, new Date());
        var displaySize = Math.floor(widthContainer / 31);
        if(displaySize < size) {
            $("#id-ganttview-slide-container").scrollLeft((size-2)*30);
        }
    }

	function handleMethod(method, value) {
		
		if (method == "setSlideWidth") {
			var div = $("div.ganttview", this);
			div.each(function () {
				var vtWidth = $("div.ganttview-vtheader", div).outerWidth();
				$(div).width(vtWidth + value + 1);
				$("div.ganttview-slide-container", this).width(value);
			});
		}
	}

	var Chart = function(div, opts) {
		
		function render() {
			addVtHeader(div, opts.data, opts.cellHeight);
			
            var slideDiv = jQuery("<div>", {
                "id": "id-ganttview-slide-container",
                "class": "ganttview-slide-container",
                "css": { "width": opts.slideWidth + "px" }
            });
			
            dates = getDates(opts.start, opts.end);
            addHzHeader(slideDiv, dates, opts.cellWidth);
            addGrid(slideDiv, opts.data, dates, opts.cellWidth, opts.showWeekends);
            addBlockContainers(slideDiv, opts.data);
            var arrayColor = addBlocks(slideDiv, opts.data, opts.cellWidth, opts.start);
            div.append(slideDiv);
            applyLastClass(div.parent());

            return arrayColor;
		}
		
		var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

		// Creates a 3 dimensional array [year][month][day] of every day 
		// between the given start and end dates
        function getDates(start, end) {
            var dates = [];
			dates[start.getFullYear()] = [];
			dates[start.getFullYear()][start.getMonth()] = [start]
			var last = start;
			while (last.compareTo(end) == -1) {
				var next = last.clone().addDays(1);
				if (!dates[next.getFullYear()]) { dates[next.getFullYear()] = []; }
				if (!dates[next.getFullYear()][next.getMonth()]) { 
					dates[next.getFullYear()][next.getMonth()] = []; 
				}
				dates[next.getFullYear()][next.getMonth()].push(next);
				last = next;
			}
			return dates;
        }

        function addVtHeader(div, data, cellHeight) {
            var headerDiv = jQuery("<div>", { "class": "ganttview-vtheader" });
            for (var i = 0; i < data.length; i++) {
                var itemDiv = jQuery("<div>", { "class": "ganttview-vtheader-item" });
                itemDiv.append(jQuery("<div>", {
					"id":"fpt-gantt_p-"+i,//[act_code]
                    "class": "ganttview-vtheader-item-name",
                    //"css": { "height": (data.length * cellHeight) + "px","width":"360px" }
					"css": { "height":"31px","width":"315px","background":"#ddd" }
                }).append(data[i].name));

				var userDiv = jQuery("<div>", { "id":"fpt-gantt_root_u_p-"+i,"class": "ganttview-vtheader-series" });
				for (var k = 0; k < data[i].user.length; k++) {
					var blockUserDiv = jQuery("<div>",{"id":"fpt-gantt_root_i_u-"+k+"_p-"+i,"style":"float:left;background:#ddd"});//[act_code]
					blockUserDiv.append(jQuery("<div>", {//[modify_code]
					"id":"fpt-gantt_u-"+k+"_p-"+i,//[act_add]
                    "class": "ganttview-vtheader-item-name",
                    "css": { "height": "31px", "width":"147px","padding-left": "18px","background":"#ddd"}
					//"css": { "height":"31px","text-align":"center"} //[act_code]
					}).append(data[i].user[k].name));//[modify_code]
					var seriesDiv = jQuery("<div>", { "id":"fpt-gantt_root_block_i_u-"+k+"_p-"+i,"class": "ganttview-vtheader-series" });
                    var flag = false;
					for (var j = 0; j < data[i].user[k].series.length; j++) {
                       var issuesOrPlandedDiv = jQuery("<div>", {
							"id":"fpt-gantt_i-"+j+"_u-"+k+"_p-"+i,
							"class": "ganttview-vtheader-series-name"
						});

                        if(!flag && data[i].user[k].series[j].name == "Planned"){
                            seriesDiv.append(issuesOrPlandedDiv.append(jQuery("<p>",{"css":{"margin-top":"7px"}}).append(data[i].user[k].series[j].name)));
                            flag = true;
                        } else if(data[i].user[k].series[j].name != "Planned") {
                            if(data[i].user[k].series[j].name == "Project Usage") {
                               issuesOrPlandedDiv.attr("id","fpt-gantt_root_a_u-"+k+"_p-"+i);
                               issuesOrPlandedDiv.removeClass("ganttview-vtheader-series-name");
                               issuesOrPlandedDiv.append( jQuery("<div>",{
                                "id":"fpt-gantt_a_u-"+k+"_p-"+i,
								"class": "ganttview-vtheader-series-name"
							   }).append(jQuery("<div>",{"css":{"margin-top":"7px"},"id":"fpt-gantt_a_u-"+k+"_p-"+i+"_u-"+k}).append(data[i].user[k].series[j].name)));

                               var issuesDiv = jQuery("<div>",{
								"id":"fpt-gantt_root_block_a_u-"+k+"_p-"+i
							   });
                                var listAssigned = data[i].user[k].series[j].issues;
                               // var numIssue = 0;
                                for(var l = 0; l < listAssigned.length; l++) {
                                    if(listAssigned[l].name != "") {
                                       // numIssue++;
                                        issuesDiv.append(jQuery("<div>", {
                                        "title":data[i].user[k].series[j].issues[l].name,
                                        "id":"fpt-gantt_a-"+l+"_u-"+k+"_p-"+i,
                                        "class": "ganttview-vtheader-series-name"
                                        }).append(jQuery("<p>",
                                                {"css":{"margin-left":"30px","margin-top":"7px"}}).append(jQuery("<a>",
                                                {"href":"/browse/"+data[i].user[k].series[j].issues[l].key}).append("["+data[i].user[k].series[j].issues[l].key+"]"))));
                                    }
                                }
                                //issuesOrPlandedDiv.css("height",(numIssue>0?34+numIssue*31:31)+"px");
                                issuesOrPlandedDiv.append(issuesDiv);
                                seriesDiv.append(issuesOrPlandedDiv);
                            }else{
                                seriesDiv.append(issuesOrPlandedDiv.append(data[i].user[k].series[j].name));
                            }
                        }

					}
					blockUserDiv.append(seriesDiv)//[modify_code]
					userDiv.append(blockUserDiv);//[modify_code]
				}

                itemDiv.append(userDiv);
                headerDiv.append(itemDiv);
            }
            div.append(headerDiv);
        }

        function addHzHeader(div, dates, cellWidth) {
            var headerDiv = jQuery("<div>", { "class": "ganttview-hzheader" });
            var monthsDiv = jQuery("<div>", { "class": "ganttview-hzheader-months" });
            var daysDiv = jQuery("<div>", { "class": "ganttview-hzheader-days" });
            var totalW = 0;
			for (var y in dates) {
				for (var m in dates[y]) {
					var w = dates[y][m].length * cellWidth;
					totalW = totalW + w;
					monthsDiv.append(jQuery("<div>", {
						"class": "ganttview-hzheader-month",
						"css": { "width": (w - 1) + "px" }
					}).append(monthNames[m] + "/" + y));
					for (var d in dates[y][m]) {
						daysDiv.append(jQuery("<div>", { "class": "ganttview-hzheader-day" })
							.append(dates[y][m][d].getDate()));
					}
				}
			}
            monthsDiv.css("width", totalW + "px");
            daysDiv.css("width", totalW + "px");
            headerDiv.append(monthsDiv).append(daysDiv);
            div.append(headerDiv);
        }

        function cloneRow(rowId, dates, cellWidth, showWeekends) {
            var rowDiv = jQuery("<div>", { "class": "ganttview-grid-row" });
			for (var y in dates) {
				for (var m in dates[y]) {
					for (var d in dates[y][m]) {
						var cellDiv = jQuery("<div>", { "class": "ganttview-grid-row-cell", "id": "r-" + rowId + "_y-"+y+"_m-"+m+"_d-"+dates[y][m][d].getDate()});

						if (DateUtils.isWeekend(dates[y][m][d]) && showWeekends) {
							cellDiv.addClass("ganttview-weekend");
						}
						rowDiv.append(cellDiv);
					}
				}
			}

            var w = jQuery("div.ganttview-grid-row-cell", rowDiv).length * cellWidth;
            rowDiv.css("width", w + "px");
            return rowDiv;
        }

        function addGrid(div, data, dates, cellWidth, showWeekends) {
            var gridDiv = jQuery("<div>", { "class": "ganttview-grid" });
            var rowCount = 0;
            var rowDiv = cloneRow(rowCount, dates, cellWidth, showWeekends);

            /*
            var rowDiv = jQuery("<div>", { "class": "ganttview-grid-row" });
			for (var y in dates) {
				for (var m in dates[y]) {
					for (var d in dates[y][m]) {
						var cellDiv = jQuery("<div>", { "class": "ganttview-grid-row-cell" });

						if (DateUtils.isWeekend(dates[y][m][d]) && showWeekends) {
							cellDiv.addClass("ganttview-weekend");
						}
						rowDiv.append(cellDiv);
					}
				}
			}
			*/
            var w = jQuery("div.ganttview-grid-row-cell", rowDiv).length * cellWidth;
            //rowDiv.css("width", w + "px");
            gridDiv.css("width", w + "px");
            for (var i = 0; i < data.length; i++) {
				var blockRowProject = jQuery("<div>",{"id":"fpt-gantt_root_rp-"+i});
                var oneLevelDiv = cloneRow(rowCount, dates, cellWidth, showWeekends);
				blockRowProject.append(oneLevelDiv.attr("id","fpt-gantt_rp-"+i));
                rowCount = rowCount + 1;
				var blockRowsUsers = jQuery("<div>",{"id":"fpt-gantt_root_ru_rp-"+i});
                for (var j = 0; j < data[i].user.length; j++) {
					var blockRowsUser = jQuery("<div>",{"id":"fpt-gantt_root_ri_ru-"+j+"_rp-"+i});
                    var flag = false;
					for(var k = 0; k < data[i].user[j].series.length ; k++) {
                        if(!flag && data[i].user[j].series[k].name == "Planned"){
                                var TwoLevelDiv = cloneRow(rowCount, dates, cellWidth, showWeekends);
                                blockRowsUser.append(TwoLevelDiv.attr("id","fpt-gantt_ri-"+k+"_ru-"+j+"_rp-"+i));
                                rowCount = rowCount + 1;
                                flag = true;
                        } else if(data[i].user[j].series[k].name != "Planned") {
                            if(data[i].user[j].series[k].name == "Project Usage") {
                                var blockRowAssigned =  jQuery("<div>",{"id":"fpt-gantt_root_ra_ru-"+j+"_rp-"+i});
                                var TwoLevelDiv = cloneRow(rowCount, dates, cellWidth, showWeekends);
                                blockRowAssigned.append(TwoLevelDiv.attr("id","fpt-gantt_ra_ru-"+j+"_rp-"+i));
                                rowCount = rowCount + 1;
                                var blockRowIssueAssigned =  jQuery("<div>",{"id":"fpt-gantt_root_block_ra_ru-"+j+"_rp-"+i});
                                for(var l = 0; l < data[i].user[j].series[k].issues.length; l++) {
                                    if(data[i].user[j].series[k].issues[l].name!=""){
                                        var ThreeLevelDiv = cloneRow(rowCount, dates, cellWidth, showWeekends);
                                        blockRowIssueAssigned.append(ThreeLevelDiv.attr("id","fpt-gantt_ra-" + l + "_ru-"+j+"_rp-"+i));
                                        rowCount = rowCount + 1;
                                    }
                                }
                                blockRowAssigned.append(blockRowIssueAssigned);
                                blockRowsUser.append(blockRowAssigned);
                            } else {
                                var TwoLevelDiv = cloneRow(rowCount, dates, cellWidth, showWeekends);
                                blockRowsUser.append(TwoLevelDiv.attr("id","fpt-gantt_ri-"+k+"_ru-"+j+"_rp-"+i));
                                rowCount = rowCount + 1;
                            }
                        }
					}
					blockRowsUsers.append(blockRowsUser);
                }
				blockRowProject.append(blockRowsUsers);
				gridDiv.append(blockRowProject);
            }
            div.append(gridDiv);
        }

        function addBlockContainers(div, data) {
            var blocksDiv = jQuery("<div>", { "class": "ganttview-blocks" });

            for (var i = 0; i < data.length; i++) {
				var blockDataProjectDiv = jQuery("<div>", {
				"id":"fpt-gantt_root_bp-"+i
				});
				blockDataProjectDiv.append(jQuery("<div>",
				{
					"id":"fpt-gantt_bp-"+i,
					"class":"ganttview-block-container"
				}));
				var blockDataUsersDiv = jQuery("<div>",{
						"id":"fpt-gantt_root_bu_bp-"+i
				});
                for (var j = 0; j < data[i].user.length; j++) {
					var blockDataUserDiv = jQuery("<div>",{
						"id":"fpt-gantt_root_bi_bu-"+j+"_bp-"+i
					});
                    var flag = false;
					for (var k = 0; k < data[i].user[j].series.length; k++) {
                        if(!flag && data[i].user[j].series[k].name == "Planned"){
                            blockDataUserDiv.append(jQuery("<div>", {
                            "id":"fpt-gantt_bi-"+k+"_bu-"+j+"_bp-"+i,
                            "class": "ganttview-block-container"
                            }));

                            flag = true;
                        } else if(data[i].user[j].series[k].name != "Planned") {
                            if(data[i].user[j].series[k].name == "Project Usage") {
                                var blockDataAssigned = jQuery("<div>", {
                                        "id":"fpt-gantt_root_ba_bu-"+j+"_bp-"+i
                                });

                                blockDataAssigned.append(jQuery("<div>", {
                                        "id":"fpt-gantt_ba_bu-"+j+"_bp-"+i,
                                        "class": "ganttview-block-container"
                                }));

                                var blockDataIssueAssigned = jQuery("<div>", {
                                        "id":"fpt-gantt_root_block_ba_bu-"+j+"_bp-"+i
                                });
                                for(var l = 0; l < data[i].user[j].series[k].issues.length; l++) {
                                    if(data[i].user[j].series[k].issues[l].name != "") {
                                        blockDataIssueAssigned.append(jQuery("<div>", {
                                        "id":"fpt-gantt_ba-"+l+"_bu-"+j+"_bp-"+i,
                                        "class": "ganttview-block-container"
                                        }));
                                    }
                                }
                                blockDataAssigned.append(blockDataIssueAssigned);
                                blockDataUserDiv.append(blockDataAssigned);
                            } else {
                                blockDataUserDiv.append(jQuery("<div>", {
                                "id":"fpt-gantt_bi-"+k+"_bu-"+j+"_bp-"+i,
                                "class": "ganttview-block-container"
                                }));
                            }
                        }
					}
					blockDataUsersDiv.append(blockDataUserDiv);
                }
				blockDataProjectDiv.append(blockDataUsersDiv);
				blocksDiv.append(blockDataProjectDiv);
            }
            div.append(blocksDiv);
        }

        function addBlocks(div, data, cellWidth, start) {
            start = new Date(start.getFullYear(),start.getMonth(),start.getDate(),0,0,0);
            var rows = jQuery("div.ganttview-blocks div.ganttview-block-container", div);
            var rowIdx = 0;
            var arrayRed = [];
            var arrayGreen = [];
            var arrayYellow = [];
            for (var i = 0; i < data.length; i++) {
				rowIdx = rowIdx + 1;
                var sumPlannedUser = [];
                var sumAssignsUser = [];
                var rowIdTemp = 0;
                for (var j = 0; j < data[i].user.length; j++) {
                    rowIdx = rowIdx + 1;
                    rowIdTemp = rowIdTemp + 1;
                    var plans = [];
                    var flag = false;
					for (var k = 0; k < data[i].user[j].series.length; k++) {
						var series = data[i].user[j].series[k];
                        series.start = new Date(series.start.getFullYear(),series.start.getMonth(),series.start.getDate(),0,0,0);
                        series.end = new Date(series.end.getFullYear(),series.end.getMonth(),series.end.getDate(),0,0,0);
						var sizeZero = DateUtils.daysBetween(start, series.start);
                        var size = DateUtils.daysBetween(series.start, series.end) + 1;
                        var sizeTotal = DateUtils.daysBetween(start, series.end) + 1;
                        var totalHour = series.name != "Planned" ? round(parseFloat(series.totalHour)/(size * 3600)) : series.totalHour / 3600;

                        if(series.name == "Planned"){
                            var plan = [];
                            for (var l = 0; l < sizeTotal; l ++) {
                                if(l < sizeZero) {
                                    plan[plan.length] = 0;
                                } else {

                                    plan[plan.length] = totalHour;
                                }
                            }
                            plans[plans.length] = plan;
                            if(!flag) {
                                rowIdx = rowIdx + 1;
                                rowIdTemp = rowIdTemp + 1;
                                flag = true;
                            }
                        }
                        var assigns = [];
                        if(series.name == "Project Usage")
                        {
                            for(var l = 0; l < series.issues.length; l++) {
                                rowIdx = rowIdx + 1;
                                rowIdTemp = rowIdTemp + 1;
                                var issue = series.issues[l];
                                issue.start = new Date(series.start.getFullYear(),issue.start.getMonth(),issue.start.getDate(),0,0,0);
                                issue.end = new Date(series.end.getFullYear(),issue.end.getMonth(),issue.end.getDate(),0,0,0);
                                var sizeZero = DateUtils.daysBetween(start, issue.start);
                                var sizeTotal = DateUtils.daysBetween(start, issue.end) + 1;
                                var size = DateUtils.daysBetween(issue.start, issue.end) + 1;
                                var sizeWorkingDay = DateUtils.sizeWorkingDays(issue.start, issue.end);
                                totalHour = round(parseFloat(issue.totalHour)/(sizeWorkingDay * 3600));
                                if(series.name != "") {
                                    var assign = [];
                                    for(var m = 0; m < sizeTotal; m++) {
                                        if(m < sizeZero) {
                                            assign[assign.length] = 0
                                        } else {
                                            assign[assign.length] = totalHour;
                                        }
                                    }
                                    assigns[assigns.length] = assign;
                                }
                                if(l != 0){
                                    var end = issue.start;
                                    for(var m = 0; m < size ; m++){
                                        var offset = DateUtils.daysBetween(start, end);
                                        var block = jQuery("<div>", {
                                            "class": "ganttview-block",
                                            "title": issue.name + ", " + 1 + " days",
                                            "css": {
                                                "width": ((1 * cellWidth) - 9) + "px",
                                                "margin-left": ((offset * cellWidth) + 3) + "px"
                                            }
                                        });
                                        if(!DateUtils.isWeekend(end)) {
                                            block.append(jQuery("<div>", { "class": "ganttview-block-text" }).text(totalHour));
                                            jQuery(rows[rowIdx - 1]).append(block);

                                        }
                                        end = new Date(end.getFullYear(),end.getMonth(),end.getDate() + 1,end.getHours(),end.getMinutes(),end.getSeconds());
                                    }
                                }

                            }

                            var assign = sumPlanned(assigns);
                            for(var l = 0; l < assign.length ; l++) {

                                var end = new Date(start.getFullYear(),start.getMonth(), start.getDate() + l);
                                var offset = DateUtils.daysBetween(start, end);
                                var block = jQuery("<div>", {
                                    "class": "ganttview-block",
                                    "title": series.name + ", " + 1 + " days",
                                    "css": {
                                        "width": ((1 * cellWidth) - 9) + "px",
                                        "margin-left": ((offset * cellWidth) + 3) + "px"
                                    }
                                });

                                if(assign[l] != 0){
//                                    if(parseFloat(assign[l]) > 8) {
//                                        block.append(jQuery("<div>", { "class": "ganttview-block-text","css": {"color":"red"} }).text(assign[l]));
//                                    } else if(parseFloat(assign[l]) < 4) {
//                                        block.append(jQuery("<div>", { "class": "ganttview-block-text","css": {"color":"green"} }).text(assign[l]));
//                                    } else {
//                                        block.append(jQuery("<div>", { "class": "ganttview-block-text" }).text(assign[l]));
//                                    }
                                    block.append(jQuery("<div>", { "class": "ganttview-block-text","css": {"color":"black"} }).text(assign[l]));
                                    if(!DateUtils.isWeekend(end)) {
                                        jQuery(rows[rowIdx - series.issues.length]).append(block);

                                        var plannedTemp = plans[plans.length - 1];
                                        plannedTemp[l] = typeof plannedTemp[l] == "undefined" ? 0 : plannedTemp[l];

                                        if(parseFloat(assign[l]) > parseFloat(plannedTemp[l])) {
                                            arrayRed[arrayRed.length] = "r-" + (rowIdx - series.issues.length) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                        } else if(parseFloat(assign[l]) == parseFloat(plannedTemp[l])) {
                                            arrayGreen[arrayGreen.length] = "r-" + (rowIdx - series.issues.length) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                        } else {
                                            arrayYellow[arrayYellow.length] = "r-" + (rowIdx - series.issues.length) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                        }
                                    }
                                }
                                end = new Date(end.getFullYear(),end.getMonth(),end.getDate() + 1,end.getHours(),end.getMinutes(),end.getSeconds());
                            }
                            sumAssignsUser[sumAssignsUser.length] = assign;
                        }
					}

                    var plan = sumPlanned(plans);
                    for(var l = 0; l < plan.length ; l++){
                        var end = new Date(start.getFullYear(),start.getMonth(), start.getDate() + l);
                        var offset = DateUtils.daysBetween(start, end);
                        var block = jQuery("<div>", {
                            "class": "ganttview-block",
                            "title": series.name + ", " + 1 + " days",
                            "css": {
                                "width": ((1 * cellWidth) - 9) + "px",
                                "margin-left": ((offset * cellWidth) + 3) + "px"
                            }
                        });

                        if(plan[l] != 0){
                            if (!DateUtils.isWeekend(end)){
                                var assigns = data[i].user[j].series[data[i].user[j].series.length - 1];
                                block.append(jQuery("<div>", { "class": "ganttview-block-text","css": {"color":"black"} }).text(plan[l]));
                                jQuery(rows[rowIdx - assigns.issues.length - 1]).append(block);
                                if(parseFloat(plan[l]) > 8){
                                    arrayRed[arrayRed.length] = "r-" + (rowIdx - assigns.issues.length - 1) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                } else if (parseFloat(plan[l]) == 8){
                                    arrayGreen[arrayGreen.length] = "r-" + (rowIdx - assigns.issues.length - 1) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                } else {
                                    arrayYellow[arrayYellow.length] = "r-" + (rowIdx - assigns.issues.length - 1) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                }
                            }
                        }
                    }
                    sumPlannedUser[sumPlannedUser.length] = plan;

                }

                if(data[i].type == "user") {
                    var planUser = sumPlanned(sumPlannedUser);
                    for(var j = 0; j < planUser.length; j++) {
                        var end = new Date(start.getFullYear(),start.getMonth(), start.getDate() + j);
                        var offset = DateUtils.daysBetween(start, end);
                        var block = jQuery("<div>", {
                            "class": "ganttview-block",
                            "title": 1 + " days",
                            "css": {
                                "width": ((1 * cellWidth) - 9) + "px",
                                "margin-left": ((offset * cellWidth) + 3) + "px"
                            }
                        });
                        if(planUser[j] != 0) {
                            if(!DateUtils.isWeekend(end)) {
                                block.append(jQuery("<div>", { "class": "ganttview-block-text", "css": {"color":"black"} }).text(planUser[j]));
                                jQuery(rows[rowIdx - rowIdTemp]).append(block);
                                if(parseFloat(planUser[j]) > 8){
                                    arrayRed[arrayRed.length] = "r-" + (rowIdx - rowIdTemp) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                } else if (parseFloat(planUser[j]) == 8){
                                    arrayGreen[arrayGreen.length] = "r-" + (rowIdx - rowIdTemp) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                } else {
                                    arrayYellow[arrayYellow.length] = "r-" + (rowIdx - rowIdTemp ) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                }
                            }
                        }
                    }

                    var assignUser = sumPlanned(sumAssignsUser);
                    for(var j = 0; j< assignUser.length; j++) {
                        var end = new Date(start.getFullYear(),start.getMonth(), start.getDate() + j);
                        var offset = DateUtils.daysBetween(start, end);
                        var block = jQuery("<div>", {
                            "class": "ganttview-block",
                            "title": 1 + " days",
                            "css": {
                                "width": ((1 * cellWidth) - 9) + "px",
                                "margin-left": ((offset * cellWidth) + 3) + "px"
                            }
                        });
                        if(assignUser[j] != 0) {
                            if(!DateUtils.isWeekend(end)) {
                                block.append(jQuery("<div>", { "class": "ganttview-block-text", "css": {"color":"black"} }).text(assignUser[j]));
                                jQuery(rows[rowIdx - rowIdTemp + 1]).append(block);

                                planUser[j] = typeof planUser[j] == "undefined" ? 0 : planUser[j];

                                if(parseFloat(assignUser[j]) > parseFloat(planUser[j])) {
                                    arrayRed[arrayRed.length] = "r-" + (rowIdx - rowIdTemp + 1) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                } else if(parseFloat(assignUser[j]) == parseFloat(planUser[j])) {
                                    arrayGreen[arrayGreen.length] = "r-" + (rowIdx - rowIdTemp + 1) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                } else {
                                    arrayYellow[arrayYellow.length] = "r-" + (rowIdx - rowIdTemp + 1) + "_y-" + end.getFullYear() + "_m-" + end.getMonth() + "_d-" + end.getDate();
                                }
                            }
                        }
                    }
                }
            }
            var arrayColor = [arrayRed,arrayGreen,arrayYellow];
            return arrayColor;
        }

        function sumPlanned(data){
            var plan = [];
            var length = 0;
            for( var i = 0; i < data.length; i++){
                if(length <= data[i].length) {
                    length = data[i].length;
                }
            }

            for(var i = 0; i < length; i++) {
                var sum = 0;
                for(var j = 0; j < data.length; j++) {
                    if(typeof(data[j][i]) == "undefined"){
                        sum = sum + 0;
                    } else {
                        sum = sum + data[j][i];
                    }
                }
                plan[i] = round(sum);
            }

            return plan;
        }

        function a(plans){
            while(1 == 1){

            }
        }

        function round(number){
            return Math.round(number * 10)/10;
        }

        function addBlockData(block, data, series) {
        	// This allows custom attributes to be added to the series data objects
        	// and makes them available to the 'data' argument of click, resize, and drag handlers
        	var blockData = { id: data.id, name: data.name };
        	jQuery.extend(blockData, series);
        	block.data("block-data", blockData);
        }

        function applyLastClass(div) {
            jQuery("div.ganttview-grid-row div.ganttview-grid-row-cell:last-child", div).addClass("last");
            jQuery("div.ganttview-hzheader-days div.ganttview-hzheader-day:last-child", div).addClass("last");
            jQuery("div.ganttview-hzheader-months div.ganttview-hzheader-month:last-child", div).addClass("last");
        }
		
		return {
			render: render,
            array: ""
		};
	}

	var Behavior = function (div, opts) {
		
		function apply() {
			/*
			if (opts.behavior.clickable) { 
            	bindBlockClick(div, opts.behavior.onClick); 
        	}
        	
            if (opts.behavior.resizable) { 
            	bindBlockResize(div, opts.cellWidth, opts.start, opts.behavior.onResize); 
        	}
            
            if (opts.behavior.draggable) { 
            	bindBlockDrag(div, opts.cellWidth, opts.start, opts.behavior.onDrag); 
        	}
        	*/
		}

        function bindBlockClick(div, callback) {
            /*
            jQuery("div.ganttview-block", div).live("click", function () {
                if (callback) { callback(jQuery(this).data("block-data")); }
            });
            */
        }
        
        function bindBlockResize(div, cellWidth, startDate, callback) {
            /*
        	jQuery("div.ganttview-block", div).resizable({
        		grid: cellWidth, 
        		handles: "e,w",
        		stop: function () {
        			var block = jQuery(this);
        			updateDataAndPosition(div, block, cellWidth, startDate);
        			if (callback) { callback(block.data("block-data")); }
        		}
        	});
        	*/
        }
        
        function bindBlockDrag(div, cellWidth, startDate, callback) {
            /*
        	jQuery("div.ganttview-block", div).draggable({
        		axis: "x", 
        		grid: [cellWidth, cellWidth],
        		stop: function () {
        			var block = jQuery(this);
        			updateDataAndPosition(div, block, cellWidth, startDate);
        			if (callback) { callback(block.data("block-data")); }
        		}
        	});
        	*/
        }
        
        function updateDataAndPosition(div, block, cellWidth, startDate) {
            /*
        	var container = jQuery("div.ganttview-slide-container", div);
        	var scroll = container.scrollLeft();
			var offset = block.offset().left - container.offset().left - 1 + scroll;
			
			// Set new start date
			var daysFromStart = Math.round(offset / cellWidth);
			var newStart = startDate.clone().addDays(daysFromStart);
			block.data("block-data").start = newStart;

			// Set new end date
        	var width = block.outerWidth();
			var numberOfDays = Math.round(width / cellWidth) - 1;
			block.data("block-data").end = newStart.clone().addDays(numberOfDays);
			jQuery("div.ganttview-block-text", block).text(numberOfDays + 1);
			
			// Remove top and left properties to avoid incorrect block positioning,
        	// set position to relative to keep blocks relative to scrollbar when scrolling
			block.css("top", "").css("left", "")
				.css("position", "relative").css("margin-left", offset + "px");
				*/
        }
        
        return {
        	apply: apply	
        };
	}

    var ArrayUtils = {
	
        contains: function (arr, obj) {
            var has = false;
            for (var i = 0; i < arr.length; i++) { if (arr[i] == obj) { has = true; } }
            return has;
        }
    };

    var DateUtils = {
    	
        daysBetween: function (start, end) {
            if (!start || !end) { return 0; }
            start = Date.parse(start); end = Date.parse(end);
            if (start.getYear() == 1901 || end.getYear() == 8099) { return 0; }
            var count = 0, date = start.clone();
            while (date.compareTo(end) == -1) {

                count = count + 1;
                date.addDays(1);
            }
            return count;
        },

        sizeWorkingDays: function (start, end) {
            if (!start || !end) { return 0; }
            start = Date.parse(start); end = Date.parse(end);
            if (start.getYear() == 1901 || end.getYear() == 8099) { return 0; }
            var count = 0, date = start.clone();
            end.addDays(1);
            while (date.compareTo(end) == -1) {
                if(!DateUtils.isWeekend(date)){
                    count = count + 1;
                }
                date.addDays(1);
            }
            return count;
        },

        isWeekend: function (date) {
            return date.getDay() % 6 == 0;
        },

		getBoundaryDatesFromData: function (data, minDays) {
			var minStart = new Date(); var maxEnd = new Date();
			for (var i = 0; i < data.length; i++) {
				for (var j = 0; j < data[i].user.length; j++) {
					for(var k = 0; k < data[i].user[j].series.length; k++) {
						var start = Date.parse(data[i].user[j].series[k].start);
						var end = Date.parse(data[i].user[j].series[k].end);
						if (i == 0 && j == 0 && k == 0) { minStart = start; maxEnd = end; }
						if (minStart.compareTo(start) == 1) { minStart = start; }
						if (maxEnd.compareTo(end) == -1) { maxEnd = end; }
                        if(data[i].user[j].series[k].name == "Project Usage") {
                            for(var l = 0; l < data[i].user[j].series[k].issues.length; l++) {
                                var startIssue = Date.parse(data[i].user[j].series[k].issues[l].start);
                                var endIssue = Date.parse(data[i].user[j].series[k].issues[l].end);
                                if (i == 0 && j == 0 && k == 0 && l == 0) { minStart = start; maxEnd = end; }
                                if (minStart.compareTo(startIssue) == 1) { minStart = startIssue; }
                                if (maxEnd.compareTo(endIssue) == -1) { maxEnd = endIssue; }
                            }
                        }
					}
				}
			}
			// Insure that the width of the chart is at least the slide width to avoid empty
			// whitespace to the right of the grid
			if (DateUtils.daysBetween(minStart, maxEnd) < minDays) {
				maxEnd = minStart.clone().addDays(minDays);
			}
			
			return [minStart, maxEnd];
		}
    };

})(jQuery);