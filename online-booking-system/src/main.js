import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus' //导入 ElementPlus 组件库的所有模块和功能
import 'element-plus/dist/index.css' //导入 ElementPlus 组件库所需的全局 CSS 样式
import * as ElementPlusIconsVue from '@element-plus/icons-vue' //导入 ElementPlus 组件库中的所有图标
import { createPersistedState } from 'pinia-plugin-persistedstate';
import router from './router/router.js';
import App from './App.vue';

const app = createApp(App);
//注册 ElementPlus 组件库中的所有图标到全局 Vue 应用中
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
const pinia = createPinia();
pinia.use(createPersistedState());
app.use(pinia);
app.use(router);
app.use(ElementPlus);
app.mount('#app');