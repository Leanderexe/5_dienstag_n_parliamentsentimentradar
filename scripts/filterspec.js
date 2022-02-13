var but=document.getElementById("newdash");
var val;
but.addEventListener("click",testfornow);
function updateVal(title){
val=title.value
};
function testfornow(){
document.getElementById("dashname").innerHTML=val;
};
