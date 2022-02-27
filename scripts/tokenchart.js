var xhr2;
var ctx;
var myLineChart;
tokenfilter("");
const start = new Date("10/24/2017");
const end = new Date("02/18/2022");

let loop = new Date(start);
while (loop <= end) {
  dat.push(loop);
  let newDate = loop.setDate(loop.getDate() + 1);
  loop = new Date(newDate);
}
document.getElementById("starttime").innerHTML=dat[0];
document.getElementById("endtime").innerHTML=dat[1577];
document.getElementById("start").value=0;
document.getElementById("end").value=1577;
function updateStart(range){
console.log(range.value);
document.getElementById("starttime").innerHTML=dat[range.value];
};

function updateEnd(range){
console.log(range.value);
document.getElementById("endtime").innerHTML=dat[range.value];
};
function tokenfilter(fil){
    if(fil==undefined){
    fil="";
    }
    console.log(fil);
    xhr2 = new XMLHttpRequest();
    lab=[];
    dat2=[];
    xhr2.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc2=JSON.parse(this.responseText);
            for(let i=0;i<spc2.length;i++){
            if(fil==""||spc2[i].Token.includes(fil)){
               lab.push(spc2[i].Token);
               dat2.push(spc2[i].Häufigkeit);
            }
            }
            console.log(lab);
       }
    };
    xhr2.open("GET", "http://localhost:4567/token", true);
    xhr2.send();
    dat=[];
    ctx = document.getElementById("tokenchart");
    myLineChart = new Chart(ctx, {
    type: 'line',
    data: {
        labels: lab,
            datasets: [{
            label: "Häufigkeit",
            lineTension: 0.1,
      backgroundColor: "rgba(78, 115, 223, 0.05)",
      borderColor: "rgba(78, 115, 223, 1)",
      data: dat2,
    }],
  },
  options: {
    maintainAspectRatio: false,
    layout: {
      padding: {
        left: 10,
        right: 25,
        top: 25,
        bottom: 0
      }
    },
    scales: {

    },
    legend: {
      display: false
    },
    tooltips: {

    }
  }
});
}