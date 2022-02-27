var buttex=document.getElementById("speechsearch");
buttex.addEventListener("click",redsearch);
var tex;
var str2;
var ctxs=document.getElementById("speechtext");
var ldi=document.getElementById("ladenind");
ctxs.innerHTML="";
function updateText(title){
tex=title.value;
console.log(tex);
};

function redsearch(){
console.log("Search started for "+tex);
var xhr7 = new XMLHttpRequest();
xhr7.onreadystatechange = function() {
        ldi.innerHTML="Reden-Volltextsuche(Lädt...)";
        console.log(this.status);
        if (this.readyState == 4 && this.status == 200) {
            ldi.innerHTML="Reden-Volltextsuche";
            var spc7=JSON.parse(this.responseText);
            ctxs.innerHTML="Das Programm hat "+spc7.length+" Rede(n) mit diesem Text gefunden!"
            for(let i=0;i<spc7.length;i++){
                  console.log(spc7[i]);
                  var xhr71 = new XMLHttpRequest();
                  xhr71.onreadystatechange = function() {
                          console.log(this.status);
                          if (this.readyState == 4 && this.status == 200) {
                              var spc71=JSON.parse(this.responseText);
                              str2=" Rede "+spc7[i].redeID+"(";
                              str2+=spc71[0].vorname+" "+spc71[0].nachname+" ("+spc71[0].fraktion+")";
                              var img=document.createElement("img");
                              img.src=spc71[0].image;
                              img.width="50";
                              img.height="30";
                              console.log(spc71[0]);
                              ctxs.innerHTML+=str2.bold();
                              document.getElementById("speechtext").appendChild(img);
                              str2="): ";
                              ctxs.innerHTML+=str2.bold();
                              for(let j=0;j<spc7[i].content.length;j++){
                                  ctxs.innerHTML+=spc7[i].content[j];
                              }
                         }
                      };
                  xhr71.open("GET", "http://localhost:4567/redner/id/"+spc7[i].rednerID, true);
                  xhr71.send();
            }
       }
    };
xhr7.open("GET", "http://localhost:4567/rede/content/"+tex, true);
xhr7.send();
};

