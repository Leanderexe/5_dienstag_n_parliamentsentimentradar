document.getElementById("starttime3").innerHTML=dat[0];
document.getElementById("endtime3").innerHTML=dat[1577];
document.getElementById("start3").value=0;
document.getElementById("end3").value=1577;
function updateStart3(range){
console.log(range.value);
document.getElementById("starttime3").innerHTML=dat[range.value];
};
function updateEnd3(range){
console.log(range.value);
document.getElementById("endtime3").innerHTML=dat[range.value];
};
slab=[];
sdat=[];
sbc=[];
sboc=[];
var xhr4 = new XMLHttpRequest();
xhr4.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc4=JSON.parse(this.responseText);
            console.log(spc4);
            for(let i=0;i<spc4.length;i++){
                slab.push(spc4[i].sentiment);
                sdat.push(spc4[i].Häufigkeit);
                sbc.push('rgba(42, 202, 0, 0.2)');
                sboc.push('rgba(42, 202, 0, 1)');
            }

       }
    };
xhr4.open("GET", "http://localhost:4567/sentiment", true);
xhr4.send();
var ctx2=document.getElementById("sentimentschart");
var senChart = new Chart(ctx2, {
     type: 'radar',
     data: {
         labels: slab,
         datasets: [{
             label: 'Häufigkeit',
             data: sdat,
             backgroundColor: sbc,
             borderColor: sboc,
             borderWidth: 1
         }]
     },

 });