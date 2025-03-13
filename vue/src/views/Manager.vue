<template>
  <div class="manager-container">
    <!-- 头部 -->
    <div class="manager-header">
      <div class="manager-header-left">
        <img src="@/assets/imgs/logo.png" />
        <div class="title">后台管理系统</div>
      </div>

      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-header-right">
        <el-dropdown placement="bottom">
          <div class="avatar">
            <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div>{{ user.name || '管理员' }}</div>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="goToPerson">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- 主体 -->
    <div class="manager-main">
      <!-- 侧边栏 -->
      <div class="manager-main-left">
        <el-menu :default-openeds="['info', 'user']" router style="border: none" :default-active="$route.path">
          <el-menu-item index="/manager">
            <i class="el-icon-s-home"></i>
            <span slot="title">系统首页</span>
          </el-menu-item>
          <el-submenu index="info">
            <template slot="title">
              <i class="el-icon-menu"></i><span>信息管理</span>
            </template>
            <el-menu-item index="/notice">公告信息</el-menu-item>
          </el-submenu>

          <el-submenu index="user">
            <template slot="title">
              <i class="el-icon-menu"></i><span>成员管理</span>
            </template>
            <el-menu-item index="/admin">用户管理</el-menu-item>
          </el-submenu>
          <el-menu-item index="/articleInfo">
            <i class="el-icon-document"></i>
            <span slot="title">文章信息</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 数据表格 -->
      <div class="manager-main-right">
        <router-view @update:user="updateUser" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Manager",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login')
    }
  },
  methods: {
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')
    },
    goToPerson() {
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson')
      }
    },
    logout() {
      localStorage.removeItem('xm-user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.manager-container {
  height: 100vh;
  background-color: #ffffff;
  display: flex;
  flex-direction: column;
}

.manager-header {
  height: 60px;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.manager-header-left {
  display: flex;
  align-items: center;
}

.manager-header-left img {
  height: 40px;
  margin-right: 10px;
}

.manager-header-left .title {
  font-size: 20px;
  color: #1890FF;
  font-weight: bold;
}

.manager-header-center {
  flex: 1;
  padding-left: 20px;
}

.manager-header-right .avatar {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.manager-header-right .avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 8px;
}

.manager-header-right .avatar div {
  color: #000000;
}

.manager-main {
  flex: 1;
  display: flex;
}

.manager-main-left {
  width: 200px;
  background-color: #ffffff;
}

.manager-main-left .el-menu {
  height: 100%;
  background-color: #ffffff;
}

.manager-main-left .el-menu-item,
.manager-main-left .el-submenu__title {
  color: #000000;
}

.manager-main-left .el-menu-item:hover,
.manager-main-left .el-submenu__title:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}

.manager-main-left .el-menu-item.is-active {
  background-color: #e6f7ff;
  color: #1890ff;
}

.manager-main-right {
  flex: 1;
  background-color: #ffffff;
  overflow-y: auto;
}

.el-breadcrumb {
  font-size: 14px;
}

.el-dropdown-menu__item:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}
</style>