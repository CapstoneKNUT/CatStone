async function get1(bno) {

    const result = await axios.get(`/replies/list/${bno}`)

    //console.log(result)

    return result;
}

async function getList({bno, page, size, goLast, type}){

    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size, type}})

    if(goLast){
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))

        return getList({bno:bno, page:lastPage, size:size, type:type})

    }

    return result.data
}


async function addReply(replyObj) {
    const response = await axios.post(`/replies/`,replyObj)
    return response.data
}

async function getReply(rno) {
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

async function modifyReply(replyObj) {

    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}

async function removeReply(rno) {
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}
