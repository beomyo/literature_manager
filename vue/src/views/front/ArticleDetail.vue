<template>
  <div class="article-detail">
    <div class="detail-card">
      <h2 class="gradient-title">{{ title }}</h2>
      <el-divider class="custom-divider"></el-divider>


      <!-- 下载按钮区域 -->
      <div class="download-section" v-if="filePaths">
        <h3>文件下载</h3>
        <div class="download-buttons">
          <el-button v-if="filePaths.patha" type="primary" size="small" @click="downloadFile('patha', title, filePaths.patha)">
            下载原文
          </el-button>
          <el-button v-if="filePaths.pathb" type="success" size="small" @click="downloadFile('pathb', title, filePaths.pathb)">
            下载附件
          </el-button>
          <el-button v-if="filePaths.pathdocx" type="warning" size="small" @click="downloadFile('pathdocx', title, filePaths.pathdocx)">
            下载DOCX
          </el-button>
          <el-button v-if="filePaths.pathpdf" type="info" size="small" @click="downloadFile('pathpdf', title, filePaths.pathpdf)">
            下载PDF
          </el-button>
        </div>
      </div>

      <!-- 对比页面顶部教师模型展示 -->
      <div v-if="isComparison" class="model-display-section">
        <h3>评判模型</h3>
        <div class="model-list">
          <div class="model-item">
            <span class="model-label">教师模型:</span>
            <span class="model-value">{{ teacherSummary.model || '未指定模型' }}</span>
          </div>
          <div class="model-item" v-for="(student, index) in studentSummaries" :key="index">
            <span class="model-label">学生模型{{ index + 1 }}:</span>
            <span class="model-value">
              {{ student.model || '未指定模型' }}
              <span class="model-position">{{ index === 0 ? '（左侧）' : '（右侧）' }}</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 单个summary展示 -->
      <div v-if="!isComparison">
        <div class="section" v-for="(group, groupName) in fieldGroups" :key="groupName" v-if="hasContent(summary, group.fields)">
          <h3>{{ group.title }}</h3>
          <p v-for="(field, index) in group.fields" :key="index" v-if="summary[field]"
             :data-type="groupName" :data-label="fieldDisplayMap[field] || field">
            {{ summary[field] }}
          </p>
        </div>
      </div>

      <!-- 对比展示 -->
      <div v-if="isComparison" class="comparison-section">
        <div class="field-group" v-for="(group, groupName) in fieldGroups" :key="groupName">
          <h3>{{ group.title }}</h3>
          <div class="field" v-for="(field, fieldIndex) in group.fields" :key="fieldIndex">
            <div v-if="studentSummaries.some(student => student[field])" class="field-row">
              <div class="field-label" :data-label="fieldDisplayMap[field] || field"></div>
              <div class="field-content">
                <div v-for="(student, index) in studentSummaries" :key="index"
                     class="content-item"
                     :class="{ 'highlight-card': teacherSummary[field] === (index + 1).toString() }">
                  <div>{{ student[field] || '无内容' }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ArticleDetail',
  data() {
    return {
      title: '',
      summary: {},
      filePaths: {},
      isComparison: false,
      teacherSummary: {},
      studentSummaries: [],
      fieldGroups: {
        // title: { title: '标题', fields: ['title'] },
        keyword: { title: '关键词', fields: ['keyword'] },
        target: { title: '研究目标', fields: ['target'] },
        summary: { title: '摘要', fields: ['summary'] },
        short: { title: '30字总结', fields: ['short1', 'short2', 'short3', 'short4', 'short5', 'short6'] },
        mid: { title: '50字总结', fields: ['mid1', 'mid2', 'mid3', 'mid4', 'mid5', 'mid6'] },
        long: { title: '100字总结', fields: ['long1', 'long2', 'long3', 'long4', 'long5', 'long6'] },
        algmid: { title: '算法 50字', fields: ['algmid1', 'algmid2', 'algmid3', 'algmid4'] },
        alglong: { title: '算法 100字', fields: ['alglong1', 'alglong2', 'alglong3', 'alglong4'] },
        environment: { title: '研究环境', fields: ['environment'] },
        tools: { title: '研究工具', fields: ['tools'] },
        datas: { title: '研究数据', fields: ['datas'] },
        standard: { title: '实验指标', fields: ['standard'] },
        result: { title: '实验结果', fields: ['result'] },
        future: { title: '未来展望', fields: ['future'] },
        weekpoint: { title: '不足之处', fields: ['weekpoint'] }
      },
      fieldDisplayMap: {
        // 'title': '标题',
        'keyword': '关键词',
        'target': '研究目标',
        'summary': '摘要',
        'short1': '简短介绍1', 'short2': '简短介绍2', 'short3': '简短介绍3', 'short4': '简短介绍4', 'short5': '简短介绍5', 'short6': '通俗版',
        'mid1': '中等介绍1', 'mid2': '中等介绍2', 'mid3': '中等介绍3', 'mid4': '中等介绍4', 'mid5': '中等介绍5', 'mid6': '通俗版',
        'long1': '详细介绍1', 'long2': '详细介绍2', 'long3': '详细介绍3', 'long4': '详细介绍4', 'long5': '详细介绍5', 'long6': '通俗版',
        'algmid1': '算法简述1', 'algmid2': '算法简述2', 'algmid3': '算法简述3', 'algmid4': '通俗版',
        'alglong1': '算法详述1', 'alglong2': '算法详述2', 'alglong3': '算法详述3', 'alglong4': '通俗版',
        'environment': '研究环境',
        'tools': '研究工具',
        'datas': '研究数据',
        'standard': '实验指标',
        'result': '实验结果',
        'future': '未来展望',
        'weekpoint': '不足之处'
      }
    }
  },
  computed: {
    hasContent() {
      return (summary, fields) => fields.some(field => summary[field])
    }
  },
  created() {
    this.title = this.$route.params.id
    if (this.title) {
      this.loadArticleSummary(this.title)
      this.loadFilePaths(this.title)
    }
  },
  methods: {
    async loadArticleSummary(title) {
      try {
        const res = await request.post(`/article/summary/${title}`)
        const summaries = res.data || []
        if (summaries.length === 1) {
          this.summary = summaries[0]
          this.isComparison = false
        } else if (summaries.length === 3) {
          const teacherSummary = summaries.find(s => s.ifteacher === 1)
          const studentSummaries = summaries.filter(s => s.ifteacher === 0)
          if (teacherSummary && studentSummaries.length === 2) {
            this.teacherSummary = teacherSummary
            this.studentSummaries = studentSummaries
            this.isComparison = true
          } else {
            this.$message.error('数据格式错误')
          }
        } else {
          this.$message.error('数据数量错误')
        }
      } catch (error) {
        this.$message.error('获取文章详情失败')
        console.error('Error fetching article summary:', error)
      }
    },
    async loadFilePaths(title) {
      try {
        const res = await request.get(`/article/file-paths/${title}`)
        this.filePaths = res.data || {}
      } catch (error) {
        this.$message.error('获取文件路径失败')
        console.error('Error fetching file paths:', error)
      }
    },
    async downloadFile(field, title, filePath) {
      try {
        const res = await request({
          url: `/article/download/${title}/${field}`,
          method: 'GET',
          responseType: 'blob'
        })
        const blob = new Blob([res])
        const ext = filePath.split('.').pop()
        const fileName = `${title}_${field}.${ext}`
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (error) {
        this.$message.error('文件下载失败')
        console.error('Error downloading file:', error)
      }
    }
  }
}
</script>

<style scoped>
.article-detail {
  padding: 30px;
  background: #f5f7fa;
  min-height: 100vh;
}

.detail-card {
  background: #ffffff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  max-width: 1000px;
  margin: 0 auto;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.detail-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0,0,0,0.15);
}

.gradient-title {
  text-align: center;
  background: linear-gradient(45deg, #409EFF, #36C6D9);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-size: 2.2rem;
  margin-bottom: 25px;
  font-weight: 600;
  letter-spacing: 1px;
}

.custom-divider {
  margin: 25px 0;
  background-color: #e8e8e8;
  height: 2px;
}

/* 教师模型展示区域样式 */
.model-display-section {
  margin-bottom: 35px;
  padding: 20px;
  border-radius: 8px;
  background: #f8fafc;
  border-left: 4px solid #409EFF;
  transition: all 0.3s ease;
}

.model-display-section:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.model-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.model-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-label {
  color: #2c3e50;
  font-weight: 500;
  min-width: 100px;
}

.model-value {
  color: #4a5568;
  background: #e6f7ff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.9rem;
}

.section, .download-section, .field-group {
  margin-bottom: 35px;
  padding: 20px;
  border-radius: 8px;
  background: #f8fafc;
  border-left: 4px solid #409EFF;
  transition: all 0.3s ease;
}

.section:hover, .download-section:hover, .field-group:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

h3 {
  color: #2c3e50;
  margin-bottom: 18px;
  font-size: 1.3rem;
  font-weight: 500;
  display: flex;
  align-items: center;
}

h3::before {
  content: "";
  display: inline-block;
  width: 6px;
  height: 20px;
  background: #409EFF;
  margin-right: 12px;
  border-radius: 2px;
}

p {
  color: #4a5568;
  line-height: 1.8;
  margin-bottom: 15px;
  font-size: 1rem;
  position: relative;
  padding-left: 1.2rem;
  transition: color 0.3s ease;
}

p:hover {
  color: #2c3e50;
}

p::before {
  content: "•";
  color: #409EFF;
  position: absolute;
  left: 0;
  font-weight: bold;
}

.section p[data-type]::before,
.field-label[data-label]::before {
  content: attr(data-label);
  display: inline-block;
  background: #409EFF;
  color: white;
  font-size: 0.8rem;
  padding: 3px 8px;
  border-radius: 4px;
  margin-right: 12px;
  flex-shrink: 0;
  min-width: 60px;
  text-align: center;
  line-height: 1.4;
  position: static;
}

.download-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.download-buttons .el-button {
  transition: transform 0.2s ease;
}

.download-buttons .el-button:hover {
  transform: scale(1.05);
}

.comparison-section {
  display: flex;
  flex-direction: column;
}

.field {
  margin-bottom: 15px;
}

.field-row {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 20px;
  margin-bottom: 10px;
  align-items: start;
}

.field-label {
  color: #2c3e50;
  font-weight: 500;
  line-height: 1.8;
  position: relative;
  padding-left: 1.2rem;
}

.field-content {
  display: flex;
  gap: 20px;
}

.content-item {
  flex: 1;
  padding: 10px;
  border-radius: 0 6px 6px 6px;
  background: #fff;
  transition: box-shadow 0.3s ease;
}

.content-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.content-item div {
  color: #4a5568;
  line-height: 1.8;
  word-break: break-word;
}

.highlight-card {
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
}

@media (max-width: 768px) {
  .detail-card {
    padding: 25px;
    margin: 15px;
  }
  .gradient-title {
    font-size: 1.8rem;
  }
  .section, .download-section, .field-group {
    padding: 15px;
  }
  h3 {
    font-size: 1.1rem;
  }
  p {
    font-size: 0.9rem;
  }
  .download-buttons {
    flex-direction: column;
    gap: 8px;
  }
  .field-row {
    grid-template-columns: 100px 1fr;
    gap: 10px;
  }
  .field-content {
    flex-direction: column;
    gap: 10px;
  }
}
</style>