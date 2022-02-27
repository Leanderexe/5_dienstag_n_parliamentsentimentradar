var but=document.getElementById("newdash");
var uindat=document.getElementById("uindat");
var filterval;
butc=document.getElementById("closedash");
butc.style.visibility="hidden";
butc.addEventListener("click",backdash);
but.addEventListener("click",fildash);

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
filterval=title.value
};
function backdash(){
document.getElementById("dashname").innerHTML="Übersicht";
butc.style.visibility="hidden";
}
function fildash(){
document.getElementById("dashname").innerHTML=filterval;
butc.style.visibility="visible";
tokenfilter(filterval);
};
