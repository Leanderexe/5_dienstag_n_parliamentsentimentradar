var but=document.getElementById("newdash");
var uindat=document.getElementById("uindat");
var val;
but.addEventListener("click",testfornow);

$(document).keydown(function ToClick(evn){
    if (evn.keyCode==13) {
        but.click();
    }
})

function ToClick(evn){
    if (evn.keyCode==13) {
        but.click();
    }
};
function updateVal(title){
val=title.value
};
function testfornow(){
document.getElementById("dashname").innerHTML=val;
};
