/*
*init listItems
*options = {"speed":value,"easing":value,"callback":function}
*speed: "slow","fast", number.
*easing: "swing","linear".
*/
(function (jQuery) {
	jQuery.fn.treeList = function () {

		$(this).each(function(index){
            $(this).addClass("fpt-gannt_node_subtract");
            var open = true;
			$(this).click( function() {
                open = swicthClassNode($(this),open,"fpt-gannt_node_subtract","fpt-gannt_node_plus");
				$("div[id=fpt-gantt_root_u_p-"+index+"]").toggle();
				$("div[id=fpt-gantt_root_bu_bp-"+index+"]").toggle();
				$("div[id=fpt-gantt_root_ru_rp-"+index+"]").toggle();
			});
			$("div[id=fpt-gantt_root_u_p-"+index+"]").children().each(function(uindex){
                var openUserNode = true;
                var idUserNode = "div[id=fpt-gantt_u-"+uindex+"_p-"+index+"]";
                if($(idUserNode).text() != "Total Planned" &&  $(idUserNode).text() != "Total Usage"){
                    $(idUserNode).addClass("fpt-gannt_node_subtract");
                    $(idUserNode).click(function(){
                    openUserNode = swicthClassNode($(idUserNode),openUserNode,"fpt-gannt_node_subtract","fpt-gannt_node_plus");
					$("div[id=fpt-gantt_root_block_i_u-"+uindex+"_p-"+index+"]").children().not(":first").toggle();
					$("div[id=fpt-gantt_root_bi_bu-"+uindex+"_bp-"+index+"]").children().not(":first").toggle();
					$("div[id=fpt-gantt_root_ri_ru-"+uindex+"_rp-"+index+"]").children().not(":first").toggle();
				    });
                    var openAssignedNode = true;
                    var idAssignedNode = "#fpt-gantt_a_u-"+uindex+"_p-"+index;
                    var idAssignedChildNode = "#fpt-gantt_a_u-"+uindex+"_p-"+index + "_u-"+uindex;
                    $(idAssignedChildNode).addClass("fpt-gannt_node_subtract");
                    $("#fpt-gantt_a_u-"+uindex+"_p-"+index).click(function(){
                       openAssignedNode = swicthClassNode($(idAssignedChildNode),openAssignedNode,"fpt-gannt_node_subtract","fpt-gannt_node_plus");
                       $("#fpt-gantt_root_block_a_u-"+uindex+"_p-"+index).toggle();
                       $("#fpt-gantt_root_block_ba_bu-"+uindex+"_bp-"+index).toggle();
                       $("#fpt-gantt_root_block_ra_ru-"+uindex+"_rp-"+index).toggle();
                    });
                    $("#fpt-gantt_a_u-"+uindex+"_p-"+index).click();
                }
			});
		});
	}

    function swicthClassNode(objectJQuerySelector,isOpen,classNameOpen,classNameClose){
        if(isOpen){
            objectJQuerySelector.removeClass(classNameOpen);
            objectJQuerySelector.addClass(classNameClose);
            isOpen = false;
        }else{
            objectJQuerySelector.removeClass(classNameClose);
            objectJQuerySelector.addClass(classNameOpen);
            isOpen = true;
        }
        return  isOpen;
    }
})(jQuery);