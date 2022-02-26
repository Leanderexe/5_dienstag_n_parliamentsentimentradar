plab=[];
pdat=[];
pbc=[];
pboc=[];
var xhr3 = new XMLHttpRequest();
xhr3.onreadystatechange = function() {
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            var spc3=JSON.parse(this.responseText);
            console.log(spc3);
            for(let i=0;i<spc3.length;i++){
                plab.push(spc3[i].POS);
                pdat.push(spc3[i].Häufigkeit);
                pbc.push('rgba('+(42+i)%255+', '+(i)%255+', '+(202+i)%255+' 0.2)');
                pboc.push('rgba('+(42+i)%255+', '+(i)%255+', '+(202+i)%255+' 1)');
            }

       }
    };
xhr3.open("GET", "http://localhost:4567/pos", true);
xhr3.send();
var ctx2=document.getElementById("poschart");
var myChart = new Chart(ctx2, {
     type: 'horizontalBar',
     data: {
         labels: plab,
         datasets: [{
             label: 'Häufigkeit',
             data: pdat,
             backgroundColor: pbc,
             borderColor: pboc,
             borderWidth: 1
         }]
     },

 });