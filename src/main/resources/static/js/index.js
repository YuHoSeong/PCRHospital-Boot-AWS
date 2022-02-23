document.querySelector("#btn-submit").addEventListener("click", (e) => {
    // e.preventDefault(); // 새로고침 방지

    let sidoCdNm = document.querySelector("#sidoCdNm").value;
    let sgguCdNm = document.querySelector("#sgguCdNm").value;

    // console.log(sidoCdNm);
    // console.log(sgguCdNm);

    getHospital(sidoCdNm, sgguCdNm);
})

let getHospital = async (sidoCdNm, sgguCdNm) => {
    let response = await fetch(`http://localhost:80/api/hospital?sidoCdNm=${sidoCdNm}&sgguCdNm=${sgguCdNm}`);
    let responsePasing = await response.json();

    // console.log(responsePasing);
    setHospital(responsePasing);

}

let setHospital = (responsePasing) => {
    let tbodyHospitalDom = document.querySelector("#tbody-hospital");
    tbodyHospitalDom.innerHTML = "";

    responsePasing.forEach((e) => {
        let trEL = document.createElement("tr");
        let tdEL1 = document.createElement("td");
        let tdEL2 = document.createElement("td");
        let tdEL3 = document.createElement("td");

        tdEL1.innerHTML = e.yadmNm;
        tdEL2.innerHTML = e.pcrPsblYn;
        tdEL3.innerHTML = e.addr;

        trEL.append(tdEL1);
        trEL.append(tdEL2);
        trEL.append(tdEL3);

        tbodyHospitalDom.append(trEL);
    });
}

let setSggucdnm = (responsePasing) => {
    let sgguCdNmDom = document.querySelector("#sgguCdNm");
    // 초기화하기
    sgguCdNmDom.innerHTML = "";
    responsePasing.forEach((e) => {
        // console.log(e);
        let optionEL = document.createElement("option");
        optionEL.text = e;
        sgguCdNmDom.append(optionEL);
    })
}

let getSggucdnm = async (sidoCdNm) => {
    let response = await fetch(`http://localhost:80/api/sggucdnm?sidoCdNm=${sidoCdNm}`);
    let responsePasing = await response.json();
    // console.log(responsePasing);
    setSggucdnm(responsePasing);
}

let sidoCdNmDom = document.querySelector("#sidoCdNm");
sidoCdNmDom.addEventListener("change", (e) => {
    console.log(e.target.value);
    let sidoCdNm = e.target.value;
    getSggucdnm(sidoCdNm);
});