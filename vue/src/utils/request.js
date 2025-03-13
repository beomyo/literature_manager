import axios from 'axios';
import router from "@/router";

const request = axios.create({
    baseURL: process.env.VUE_APP_BASEURL, // 后端接口地址，例如 http://localhost:9090
    timeout: 30000 // 30秒请求超时
});

// 请求拦截器
request.interceptors.request.use(config => {
    console.log('请求路径:', config.url);
    console.log('请求方法:', config.method);
    console.log('请求数据:', config.data);

    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    // 检查是否为认证请求（登录或注册请求不需要 token）
    const isAuthRequest = config.url && (
        config.url.endsWith('/api/register') ||
        config.url.endsWith('/api/login')
    );

    console.log('是否为认证请求:', isAuthRequest);

    if (!isAuthRequest) {
        const user = localStorage.getItem("xm-user");
        console.log('本地存储的用户信息:', user);
        if (user) {
            const userObj = JSON.parse(user);
            console.log('解析后的用户信息:', userObj);
            // 确保 token 被添加到请求头中
            if (userObj.token) {
                config.headers['token'] = userObj.token;
            }
            else {
                console.warn('xm-user 中缺少 token 字段');
                router.push('/login'); // 如果没有 token，跳转到登录页
                //return Promise.reject(new Error('未找到 token'));
            }
        }
        else {
            console.warn('未找到 xm-user，本地存储可能为空');
            router.push('/login'); // 如果没有 xm-user，跳转到登录页
            //return Promise.reject(new Error('用户未登录'));
        }
    }

    console.log('最终请求头:', config.headers);
    return config;
}, error => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
});

// 响应拦截器
request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        if (res.code === '401' || res.code === 401) {
            router.push('/login');
        }
        return res;
    },
    error => {
        console.error('响应错误:', error);
        if (error.response && (error.response.status === 401 || error.response.data?.code === '401')) {
            router.push('/login');
        }
        return Promise.reject(error);
    }
);

export default request;