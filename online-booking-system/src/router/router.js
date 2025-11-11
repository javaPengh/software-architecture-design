import { createRouter, createWebHistory } from 'vue-router';
import Login from "../components/Login.vue";
import Home from "../components/Home.vue";
import Register from "../components/Register.vue";
import MyOrders from "../components/MyOrders.vue";
import TicketSearch from "../components/TicketSearch.vue";
import Screening from "../components/Screening.vue";
import Movie from '../components/Movie.vue';
import Hall from '../components/Hall.vue';
import Captcha from '../components/Captcha.vue';
import { useUserStore } from '../store/userStore';
import { ElMessage } from 'element-plus';


const routes = [
  { path: '/', component: Home },
  { path: '/home', component: Home },
  { path: '/Captcha', component: Captcha },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/ticket-search', component: TicketSearch },
  { path: '/my-orders', component: MyOrders },
  { path: '/movie', component: Movie },
  { path: '/hall', component: Hall },
  { path: '/screening', component: Screening }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const userInfo = useUserStore()
  if (userInfo.token) {
    if (to.path === "/login") {
      next({ path: "/" })
    } else {
      if (userInfo.isLoggedIn) {
        next()
      } else {
        try {
          await userInfo.getUserInfo()
          next()
        } catch (error) {
          localStorage.removeItem("token");
          userInfo.$reset();
        }
      }
    }
  } else {
    if(to.path === "/login"|| to.path === "/register"){
      next()
    }else{
      ElMessage.warning("您尚未登录，请先登录")
      next({ path: "/login" })
    }
  }
});
export default router;