document.getElementById("starttime4").innerHTML=dat[0];
document.getElementById("endtime4").innerHTML=dat[1577];
document.getElementById("start4").value=0;
document.getElementById("end4").value=1577;
namedentfilter("");
function updateStart4(range){
console.log(range.value);
document.getElementById("starttime4").innerHTML=dat[range.value];
};
function updateEnd4(range){
console.log(range.value);
document.getElementById("endtime4").innerHTML=dat[range.value];
};
function namedentfilter(fil){
ndat=[];
nbc=[];
nboc=[];
ndat2=[];
nbc2=[];
nboc2=[];
ndat3=[];
nbc3=[];
nboc3=[];
nlabs=[];
var xhr51 = new XMLHttpRequest();
xhr51.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc51=JSON.parse(this.responseText);
            console.log(spc51);
            for(let i=0;i<spc51.length;i++){
                nlabs.push("Organisation:"+spc51[i].namedEntitiesObject);
                ndat.push(spc51[i].Häufigkeit);
            }
       }
    };
xhr51.open("GET", "http://localhost:4567/namedentitiesobjects/LPO/ORG", true);
xhr51.send();
var xhr52 = new XMLHttpRequest();
xhr52.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc52=JSON.parse(this.responseText);
            console.log(spc52);
            for(let i=0;i<spc52.length;i++){
                nlabs[i]+=",Ort:"+spc52[i].namedEntitiesObject;
                ndat2.push(spc52[i].Häufigkeit);
            }
       }
    };
xhr52.open("GET", "http://localhost:4567/namedentitiesobjects/LPO/LOC", true);
xhr52.send();
var xhr53 = new XMLHttpRequest();
xhr53.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc53=JSON.parse(this.responseText);
            console.log(spc53);
            for(let i=0;i<spc53.length;i++){
                nlabs[i]+=",Person:"+spc53[i].namedEntitiesObject;
                ndat3.push(spc53[i].Häufigkeit);
            }
       }
    };
xhr53.open("GET", "http://localhost:4567/namedentitiesobjects/LPO/PER", true);
xhr53.send();

var ctx = document.getElementById("namedentchart");
var neChart = new Chart(ctx, {
  type: 'line',
  data: {
    labels: nlabs,
    datasets: [{
      label: "Personen",
      lineTension: 0.1,
      backgroundColor: "rgba(78, 115, 223, 0.05)",
      borderColor: "rgba(78, 115, 223, 1)",
      pointRadius: 3,
      pointBackgroundColor: "rgba(78, 115, 223, 1)",
      pointBorderColor: "rgba(78, 115, 223, 1)",
      pointHoverRadius: 3,
      pointHoverBackgroundColor: "rgba(78, 115, 223, 1)",
      pointHoverBorderColor: "rgba(78, 115, 223, 1)",
      pointHitRadius: 10,
      pointBorderWidth: 2,
      data: ndat3,
    },
    {
          label: "Organisationen",
          lineTension: 0.1,
          backgroundColor: "rgba(178, 15, 223, 0.05)",
          borderColor: "rgba(178, 015, 223, 1)",
          pointRadius: 3,
          pointBackgroundColor: "rgba(178, 15, 223, 1)",
          pointBorderColor: "rgba(178, 15, 223, 1)",
          pointHoverRadius: 3,
          pointHoverBackgroundColor: "rgba(178, 15, 223, 1)",
          pointHoverBorderColor: "rgba(178, 15, 223, 1)",
          pointHitRadius: 10,
          pointBorderWidth: 2,
          data: ndat2,
   },
  {
            label: "Orte",
            lineTension: 0.1,
            backgroundColor: "rgba(202, 42, 0, 0.05)",
            borderColor: "rgba(202, 42, 0, 1)",
            pointRadius: 3,
            pointBackgroundColor: "rgba(202, 42, 0, 1)",
            pointBorderColor: "rgba(202, 42, 0, 1)",
            pointHoverRadius: 3,
            pointHoverBackgroundColor: "rgba(202, 42, 0, 1)",
            pointHoverBorderColor: "rgba(202, 42, 0, 1)",
            pointHitRadius: 10,
            pointBorderWidth: 2,
            data: ndat,
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
      xAxes: [{
        time: {
          unit: 'date'
        },
        gridLines: {
          display: false,
          drawBorder: false
        },
        ticks: {
          maxTicksLimit: 7
        }
      }],
      yAxes: [{
        ticks: {
          maxTicksLimit: 5,
          padding: 10,

        },
        gridLines: {
          color: "rgb(234, 236, 244)",
          zeroLineColor: "rgb(234, 236, 244)",
          drawBorder: false,
          borderDash: [2],
          zeroLineBorderDash: [2]
        }
      }],
    },
    legend: {
      display: false
    },
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      titleMarginBottom: 10,
      titleFontColor: '#6e707e',
      titleFontSize: 14,
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      intersect: false,
      mode: 'index',
      caretPadding: 10,

    }
  }
});
}