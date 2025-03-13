import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

const routes = [
    {
        path: '/',
        name: 'Manager',
        component: () => import('../views/Manager.vue'),
        redirect: '/manager',  // 重定向到主页
        children: [
            {path: '403', name: 'NoAuth', meta: {name: '无权限'}, component: () => import('../views/manager/403')},
            {
                path: 'manager',
                name: 'Manager',
                meta: {name: '系统首页'},
                component: () => import('../views/manager/Manager')
            },
            {
                path: 'admin',
                name: 'Admin',
                meta: {name: '用户管理'},
                component: () => import('../views/manager/Admin')
            },
            {
                path: 'adminPerson',
                name: 'AdminPerson',
                meta: {name: '个人信息'},
                component: () => import('../views/manager/AdminPerson')
            },
            {
                path: 'password',
                name: 'Password',
                meta: {name: '修改密码'},
                component: () => import('../views/manager/Password')
            },
            {
                path: 'notice',
                name: 'Notice',
                meta: {name: '公告信息'},
                component: () => import('../views/manager/Notice')
            },
            {
                path: 'articleInfo',
                name: 'ArticleInfo',
                meta: { name: '文章信息' },
                component: () => import('../views/manager/ArticleInfo.vue')
            },
        ]
    },
    {
        path: '/front',
        name: 'Front',
        component: () => import('../views/Front.vue'),
        children: [
            {
                path: 'home',
                name: 'Home',
                meta: {name: '系统首页'},
                component: () => import('../views/front/Home')},
            {
                path: 'person',
                name: 'Person',
                meta: {name: '个人信息'},
                component: () => import('../views/front/Person')
            },
            {
                path: 'articleedit',
                name: 'Articleedit',
                meta: {name: '个人信息'},
                component: () => import('../views/front/Articleedit')
            },
            {
                path: 'article/:id',
                name: 'ArticleDetail',
                meta: {name: '文章详情'},
                component: () => import('../views/front/ArticleDetail')
            },
            {
                path: 'upload',
                name: 'Upload',
                meta: {name: '论文上传'},
                component: () => import('../views/front/Upload')
            },
            {
                path: 'graph',
                name: 'Graph',
                meta: {name: '知识图谱'},
                component: () => import('../views/front/Graph')
            },
            {
                path: '/front/neo4j',
                name: 'Neo4j',
                component: () => import('@/views/front/Neo4jView.vue')
            },
        ]
    },
    {path: '/login', name: 'Login', meta: {name: '登录'}, component: () => import('../views/Login.vue')},
    {path: '/register', name: 'Register', meta: {name: '注册'}, component: () => import('../views/Register.vue')},
    {path: '*', name: 'NotFound', meta: {name: '无法访问'}, component: () => import('../views/404.vue')},
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})
// 路由守卫
router.beforeEach((to, from, next) => {
    // 获取用户信息
    let user = JSON.parse(localStorage.getItem("xm-user") || '{}');

    // 白名单路由，无需验证直接放行
    const whiteList = ['/login', '/register'];
    if (whiteList.includes(to.path)) {
        next();
        return;
    }

    // 验证用户信息和token
    if (!user.role || !user.token) {
        next('/login');
        return;
    }

    // 根据角色进行路由控制
    const isAdmin = user.role === 'ADMIN';
    const isUser = user.role === 'USER';

    // 处理根路径重定向
    if (to.path === '/') {
        next(isAdmin ? '/manager/manager' : '/front/home');
        return;
    }

    // 权限验证
    if ((isUser && to.path.startsWith('/manager')) ||
        (isAdmin && to.path.startsWith('/front'))) {
        next(isAdmin ? '/manager/manager' : '/front/home');
        return;
    }

    // 其他情况放行
    next();

})

export default router
