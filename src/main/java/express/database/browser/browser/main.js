import App from './src/app.js'
import store from './src/store.js'

const { createApp } = Vue
const app = createApp(App)
app.use(store)
app.mount('#app')


async function getCollectionNames() {
    let klassNames = await fetch('/rest/klassNames')
    klassNames = await klassNames.json();
    console.log(klassNames);

    for(let klass of klassNames) {
        await getCollection(klass)
    }
}

async function getCollection(coll) {
    let res = await fetch('/rest/' + coll)
    console.log(await res.json());
}
