<template>
  <div>
    <div class="header-row">
      <div class="search">
        <el-button type="danger" plain @click="delBatch">批量删除</el-button>
        <el-input placeholder="请输入标题查询" style="width: 220px" v-model="title"></el-input>
        <el-button type="info" plain @click="load(1)">查询</el-button>
        <el-button type="warning" plain @click="reset">重置</el-button>
      </div>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="title" label="标题"></el-table-column>
        <el-table-column prop="author" label="作者"></el-table-column>
        <el-table-column prop="srcDatabase" label="来源库"></el-table-column>
        <el-table-column prop="year" label="年份"></el-table-column>
        <el-table-column prop="doi" label="DOI"></el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="文章信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="来源库" prop="srcDatabase">
          <el-input v-model="form.srcDatabase" placeholder="来源库"></el-input>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="标题"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="作者"></el-input>
        </el-form-item>
        <el-form-item label="组织" prop="organ">
          <el-input v-model="form.organ" placeholder="组织"></el-input>
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="form.source" placeholder="来源"></el-input>
        </el-form-item>
        <el-form-item label="关键词" prop="keyword">
          <el-input v-model="form.keyword" placeholder="关键词"></el-input>
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input type="textarea" v-model="form.summary" placeholder="摘要"></el-input>
        </el-form-item>
        <el-form-item label="发布时间" prop="pubTime">
          <el-input v-model="form.pubTime" placeholder="发布时间"></el-input>
        </el-form-item>
        <el-form-item label="第一责任人" prop="firstDuty">
          <el-input v-model="form.firstDuty" placeholder="第一责任人"></el-input>
        </el-form-item>
        <el-form-item label="基金" prop="fund">
          <el-input v-model="form.fund" placeholder="基金"></el-input>
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-input v-model="form.year" placeholder="年份"></el-input>
        </el-form-item>
        <el-form-item label="页数" prop="pageCount">
          <el-input v-model="form.pageCount" placeholder="页数"></el-input>
        </el-form-item>
        <el-form-item label="分类号" prop="clc">
          <el-input v-model="form.clc" placeholder="分类号"></el-input>
        </el-form-item>
        <el-form-item label="URL" prop="url">
          <el-input v-model="form.url" placeholder="URL"></el-input>
        </el-form-item>
        <el-form-item label="DOI" prop="doi">
          <el-input v-model="form.doi" placeholder="DOI"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: "Articleedit",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      title: null,
      fromVisible: false,
      form: {},
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
        ],
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.fromVisible = true
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/articleInfo/update' : '/articleInfo/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/articleInfo/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id)
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/articleInfo/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {})
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
      console.log('当前用户信息:', user); // 调试：检查 xm-user 是否存在
      console.log('当前 token:', user.token); // 调试：检查 token 是否存在
      this.$request.get('/articleInfo/selectByUserId', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title,
        }
      }).then(res => {
        console.log('后端响应数据:', res); // 调试：检查后端返回的数据
        this.tableData = res.data?.list || [];
        this.total = res.data?.total || 0;
      }).catch(err => {
        console.error('请求失败:', err); // 调试：捕获请求错误
        this.$message.error('加载数据失败，请检查登录状态');
      });
    },
    reset() {
      this.title = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>
/* 整体容器样式，设置较小的页边距 */
div {
  padding: 15px; /* 整体内边距，稍微缩小 */
  max-width: 1200px; /* 最大宽度，保持内容居中 */
  margin: 0 auto; /* 水平居中 */
}

/* 头部行样式，将搜索和批量删除放在一行 */
.header-row {
  display: flex; /* 使用flex布局 */
  align-items: center; /* 垂直居中对齐 */
  justify-content: space-between; /* 两端对齐：批量删除靠左，搜索靠右 */
  margin-bottom: 20px; /* 与下方表格的间距 */
}

/* 操作区域样式（批量删除） */
.operation {
  padding: 0; /* 移除内边距，保持紧凑 */
}

/* 搜索区域样式 - 美化 */
.search {
  display: flex; /* flex布局排列输入框和按钮 */
  align-items: center; /* 垂直居中 */
  padding: 10px 15px; /* 内边距：上下10px，左右15px */
  background-color: #ffffff; /* 白色背景 */
  border-radius: 20px; /* 更大的圆角，柔和外观 */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08); /* 更柔和的阴影 */
  border: 1px solid #dcdfe6; /* Element UI默认边框颜色 */
  transition: all 0.3s; /* 添加过渡效果 */
}

/* 搜索框悬停效果 */
.search:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 悬停时阴影加深 */
}

/* 输入框样式 */
.search .el-input {
  width: 220px; /* 输入框宽度 */
  margin-right: 10px; /* 与按钮的间距 */
}

.search .el-input__inner {
  border-radius: 16px; /* 输入框圆角 */
  border: 1px solid #dcdfe6; /* 边框颜色 */
}

/* 搜索区域按钮样式 */
.search .el-button {
  margin-left: 10px; /* 按钮之间的间距 */
  border-radius: 16px; /* 圆角按钮 */
}

/* 表格区域样式 */
.table {
  margin-bottom: 20px; /* 与下方分页的间距 */
  padding: 12px; /* 内边距 */
  background-color: #ffffff; /* 白色背景 */
  border-radius: 6px; /* 圆角 */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

.el-table {
  border-radius: 6px; /* 表格圆角 */
  overflow: hidden; /* 防止内容溢出 */
}

.el-table th {
  background-color: #fafafa; /* 表头背景色 */
  font-weight: 600; /* 表头字体加粗 */
  color: #303133; /* Element UI默认深色文字 */
}

.el-table td {
  color: #606266; /* Element UI默认文字颜色 */
}

/* 分页样式 */
.pagination {
  text-align: center; /* 居中对齐 */
  margin-top: 15px; /* 与上方表格的间距 */
  padding: 8px 0; /* 内边距 */
}

/* 对话框样式 */
.el-dialog {
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 阴影 */
}

.el-dialog__header {
  background-color: #409eff; /* Element UI主色调 */
  color: #fff; /* 白色文字 */
  border-top-left-radius: 8px; /* 左上圆角 */
  border-top-right-radius: 8px; /* 右上圆角 */
  padding: 12px 20px; /* 内边距 */
}

.el-dialog__title {
  font-size: 16px; /* 标题字体大小 */
  font-weight: 500; /* 字体粗细 */
}

.el-form {
  padding: 15px; /* 表单内边距 */
}

.el-form-item {
  margin-bottom: 18px; /* 表单项间距 */
}

.el-form-item__label {
  font-weight: 500; /* 标签字体加粗 */
  color: #303133; /* 默认深色文字 */
}

.dialog-footer {
  text-align: right; /* 按钮靠右 */
  padding: 12px 20px; /* 内边距 */
  border-top: 1px solid #dcdfe6; /* 分割线 */
}

/* 按钮样式 */
.el-button {
  border-radius: 4px; /* 默认圆角 */
  transition: all 0.3s; /* 过渡效果 */
}

.el-button:hover {
  opacity: 0.9; /* 悬停时透明度 */
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

/* 响应式调整 */
@media (max-width: 768px) {
  .header-row {
    flex-direction: column; /* 小屏幕时垂直排列 */
    align-items: flex-start; /* 靠左对齐 */
  }

  .search {
    width: 100%; /* 搜索框宽度占满 */
    justify-content: center; /* 小屏幕时居中 */
    margin-top: 10px; /* 与批量删除的间距 */
  }

  .search .el-input,
  .search .el-button {
    width: 100%; /* 宽度占满 */
    margin: 8px 0; /* 垂直间距 */
  }

  .el-dialog {
    width: 90%; /* 对话框宽度调整 */
  }
}
</style>