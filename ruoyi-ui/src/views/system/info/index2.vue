<template>
  <div class="info_container">
    <div class="first_line" v-show="showSearch">
      <el-form :model="searchOptions" label-width="70px" size="small">
        <el-form-item label="办结情况">
          <el-select v-model="searchOptions.isSuccess" placeholder="请选择办结情况" @change="searchTableData">
            <el-option v-for="item in isSuccessOptions" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="问题类别">
          <el-select v-model="searchOptions.problemType" placeholder="请选择问题类别" @change="searchTableData">
            <el-option v-for="item in serviceTypeOptions" :key="item.value" :label="item.dictLabel" :value="item.dictValue">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单位">
          <el-select v-model="searchOptions.reportCompany" placeholder="请选择单位" @change="searchTableData">
            <el-option v-for="item in detailOptions" :key="item.value" :label="item.dictLabel" :value="item.dictValue">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="事件时间">
          <el-date-picker v-model="searchOptions.dateTime" type="daterange" size="small" unlink-panels range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" :picker-options="pickerOptions" @change="searchTableData" />
        </el-form-item>
        <el-form-item label="主要问题">
          <el-input v-model="searchOptions.content" placeholder="搜索举报问题" prefix-icon="el-icon-search" clearable @input="searchTableData"></el-input>
        </el-form-item>
        <div style="margin-bottom:16px">
          <!-- <el-button @click="searchTableData" type="primary" icon="el-icon-search" size="small">搜索</el-button> -->
          <el-button @click="resetList" icon="el-icon-refresh-right" size="small">重置</el-button>
        </div>
      </el-form>
    </div>
    <div class="second_line">
      <el-button @click="exportFile" type="warning" icon="el-icon-download" size="small" class="yellow_btn">导出</el-button>
      <div class="right_btn">
        <el-tooltip class="item" effect="dark" :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top">
          <el-button size="mini" circle icon="el-icon-search" @click="showSearch = !showSearch" />
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="刷新" placement="top">
          <el-button size="mini" circle icon="el-icon-refresh" @click="resetList()" />
        </el-tooltip>
      </div>
    </div>
    <el-table ref="multipleTable" :data="tableData" tooltip-effect="light" style="width: 100%" v-loading="tableLoading" element-loading-spinner="el-icon-loading">
      <el-table-column type="index" width="60" />
      <el-table-column prop="reprotPerson" label="被举报人" />
      <el-table-column prop="reportCompany" label="单位" />
      <el-table-column prop="post" label="职务" width="100" />
      <el-table-column prop="problemType" label="问题类别" align="center" />
      <el-table-column prop="content" label="举报内容" show-overflow-tooltip align="center" />
      <el-table-column label="是否实名" align="center" width="100">
        <template slot-scope="scope">{{ scope.row.isReal == 'N'?'否':'是' }}</template>
      </el-table-column>
      <el-table-column prop="evaluate" label="用户反馈">
        <template slot-scope="scope">{{scope.row.evaluate?scope.row.evaluate:'- -' }}</template>
      </el-table-column>
      <el-table-column label="办结情况" width="100" align="center">
        <template slot-scope="scope">
          <span :style="{color:scope.row.isSuccess == 'N'?'red':'#606266'}">{{scope.row.isSuccess == 'N'?'待处理':'已办结' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="办结时长" width="100" align="center">
        <template slot-scope="scope">
          <span>{{scope.row.completionTime==null?'- -':(scope.row.completionTime==0?'当天':scope.row.completionTime+'天')}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="举报时间" />
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-link v-if="$store.getters.roles != 'admin' && scope.row.isSuccess == 'N'" @click="openDialog(scope.row,'dispose')" type="primary" :underline="false" v-hasPermi="['system:report:submitReport']" icon="el-icon-edit">处理</el-link>
          <el-link @click="openDialog(scope.row,'toView')" type="primary" :underline="false" icon="el-icon-view">查看</el-link>
          <el-link type="primary" @click="exportAsingleFile(scope.row)" :underline="false" icon="el-icon-download">导出</el-link>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="searchOptions.pageNum" :limit.sync="searchOptions.pageSize" @pagination="getTableData" />
    <el-dialog :visible.sync="showDialog" :title="dialogTitle" width="560px" @close="closeDialog">
      <el-form :model="currentData" :rules="rules" ref="ruleForm" label-width="140px" label-position="right">
        <el-form-item label="问题类别:">
          {{currentData.problemType}}
        </el-form-item>
        <el-form-item label="单位:">
          {{currentData.reportCompany}}
        </el-form-item>
        <el-form-item label="职务:">
          {{currentData.post}}
        </el-form-item>
        <el-form-item label="主要问题:">
          {{currentData.content}}
        </el-form-item>
        <el-form-item label="文件信息:" class="img_list">
          <div class="dialog_img">
            <el-image :src="item" :preview-src-list="imgList" v-for="(item,index) in imgList" :key="item+index+'imgList'" class="img_list">
            </el-image>
          </div>
          <div class="dialog_img">
            <video :src="item" @click="openVideo(item)" v-for="(item,index) in videoList" :key="item.url+index+'videoList'" class="img_list"></video>
          </div>
          <div v-for="(item,index) in fileList" :key="item.fileName+index">
            <a style="color:#3093d9" :href="item.fileUrl">{{item.fileName}} </a>
          </div>
        </el-form-item>
        <el-form-item label="姓名/电话:">
          {{currentData.isReal=='Y'?`${currentData.realName} / ${currentData.phone}`:'匿名'}}
        </el-form-item>
        <el-form-item label="身份证号/政治面貌:">
          {{currentData.idCard?currentData.idCard:'用户未输入'}}&nbsp;&nbsp; / &nbsp;&nbsp;{{currentData.politicsFace?currentData.politicsFace:'用户未输入'}}
        </el-form-item>
        <el-form-item label="处理结果:" v-if="dialogTitle=='处理'" prop="opinion" key="opinion">
          <el-input v-model="currentData.opinion" @input="change($event)" type="textarea" :autosize="{minRows: 3, maxRows: 6}" maxlength="500" placeholder="请输入处理结果"></el-input>
        </el-form-item>
        <el-form-item label="处理结果:" v-else>
          {{currentData.opinion}}
        </el-form-item>
        <el-form-item label="事件处理时长:">
          {{currentData.completionTime==null?'- -':(currentData.completionTime==0?'当天':currentData.completionTime+'天')}}
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showDialog = false;currentData.opinion = ''" size="small">{{dialogTitle=='处理'?'取 消':'关 闭'}}</el-button>
        <el-button v-if="dialogTitle=='处理'" type="primary" @click="sureSubmit" size="small">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { complaintsList, submitComplaints, msjdjbAll, fileListById } from "@/api/system/info";
export default {
  name: 'info',
  data () {
    return {
      searchOptions: {
        pageNum: 1,
        pageSize: 10,
        isSuccess: '',
        content: '',
        reprotPerson: '',
        reportCompany: '',
        problemType: '',
        dateTime: ['', '']
      },
      isSuccessOptions: [
        {
          "label": "办理中",
          "value": 'N'
        }, {
          "label": "已办结",
          "value": 'Y'
        }],
      serviceTypeOptions: [],
      detailOptions: [],
      showSearch: true,
      tableData: [],
      tableLoading: true,
      total: 0,
      showDialog: false,
      dialogTitle: '',
      currentData: {
        id: '',
        opinion: '',
      },
      timer: null,
      imgList: [],
      videoList: [],
      fileList: [],
      pickerOptions: {
        shortcuts: [{
          text: '本月',
          onClick (picker) {
            const start = new Date();
            start.setDate(1);
            picker.$emit('pick', [start, new Date()]);
          }
        }, {
          text: '今年至今',
          onClick (picker) {
            const end = new Date();
            const start = new Date(new Date().getFullYear(), 0);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近六个月',
          onClick (picker) {
            const end = new Date();
            const start = new Date();
            start.setMonth(start.getMonth() - 6);
            picker.$emit('pick', [start, end]);
          }
        }],
        disabledDate (time) {
          return time.getTime(new Date()) > Date.now();
        },
      },
      rules: {
        opinion: [{ required: true, message: '请输入处理结果', trigger: 'blur', }],
      },
    }
  },
  created () {
    this.getSelectList();
    this.getTableData()
  },
  methods: {
    // 下拉框数据
    getSelectList () {
      msjdjbAll().then(res => {
        this.serviceTypeOptions = res.data.problem_type
        this.detailOptions = res.data.report_company
      });
    },
    // 表格数据
    getTableData () {
      this.tableLoading = true
      let data = {
        pageNum: this.searchOptions.pageNum,
        pageSize: this.searchOptions.pageSize,
        isSuccess: this.searchOptions.isSuccess,
        reprotPerson: this.searchOptions.reprotPerson,
        reportCompany: this.searchOptions.reportCompany,
        problemType: this.searchOptions.problemType,
        content: this.searchOptions.content,
        startTime: this.searchOptions.dateTime[0],
        endTime: this.searchOptions.dateTime[1],
      }
      complaintsList(data).then(res => {
        this.tableData = res.rows;
        this.tableLoading = false
        this.total = res.total;
      });
    },
    // 搜索按钮
    searchTableData () {
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.searchOptions.pageNum = 1
        this.getTableData()
      }, 500);
    },
    // 重置按钮
    resetList () {
      this.searchOptions = {
        pageNum: 1,
        pageSize: 10,
        isSuccess: '',
        problemType: '',
        reportCompany: '',
        content: '',
        dateTime: ['', '']
      }
      this.getTableData()
    },
    // 导出所有
    exportFile () {
      this.download('system/complaints/export', {}, `码上监督举报报表.xlsx`)
    },
    // 导出单个
    exportAsingleFile (row) {
      this.download('system/complaints/exportDocx', {
        id: row.id
      }, `举报详情信息.docx`)
    },
    // 点击处理按钮
    openDialog (row, title) {
      title == 'dispose' ? this.dialogTitle = '处理' : this.dialogTitle = '查看'
      this.currentData = row;
      this.currentData.id = row.id;
      if (title != 'dispose') this.currentData.opinion = row.opinion
      fileListById({ id: row.id }).then(res => {
        this.imgList = []
        this.videoList = []
        this.fileList = []
        res.data.map(item => {
          if (item.extension == '.mp4') {
            this.videoList.push(item.url)
          } else if (item.extension == '.jpg' || item.extension == '.png') {
            this.imgList.push(item.url)
          } else {
            this.fileList.push({
              fileName: item.fileName,
              fileUrl: item.url
            })
          }
        })
        this.showDialog = true
      })
    },
    // 公共方法
    publicFun (res) {
      if (res.code == 200) {
        this.getTableData()
        this.$message({
          message: `${res.msg}`,
          type: 'success'
        });
        this.showDialog = false
      } else {
        this.$message({
          message: `${res.msg}`,
          type: 'error'
        });
      }
    },
    // 确定处理
    sureSubmit () {
      if (this.dialogTitle == '处理') {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            let data = { id: this.currentData.id, opinion: this.currentData.opinion }
            submitComplaints(data).then((res) => {
              this.publicFun(res)
            })
          } else {
            return false;
          }
        });
      } else {
        this.showDialog = false
      }

    },
    // 新窗口打开视频
    openVideo (url) {
      let a = document.createElement("a");
      document.body.appendChild(a);
      a.style = "display: none";
      a.target = "_blank";
      a.href = url;
      a.click();
      document.body.removeChild(a);
    },
    // 关闭弹窗
    closeDialog () {
      this.$nextTick(() => {
        this.currentData.opinion = ''
        if (this.dialogTitle == '处理') {
          this.$refs.ruleForm.resetFields();
        }
      })
    },
    change (e) {
      this.$forceUpdate();
    }
  }
}
</script>

<style lang="scss" scoped>
.info_container {
  padding: 16px;
  .el-date-editor {
    width: 215px;
  }
  .first_line {
    .el-form {
      display: flex;
      flex-wrap: wrap;
      display: flex;
      .el-form-item {
        margin-right: 12px;
      }
      .el-form-item__label {
        padding: 0;
      }
      .el-input .el-input--small {
        width: 180px;
      }
      .el-button {
        height: 32px;
      }
    }
  }
  .second_line {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    .yellow_btn {
      color: #ffc077;
      background: #fff8e6;
      border: 1px solid #f9dc8f;
    }
  }
  .el-link {
    margin-left: 12px;
  }
  .dialog_img {
    display: flex;
    justify-content: flex-start;
    flex-wrap: wrap;
    .img_list {
      width: 100%;
      height: 100%;
      max-width: 120px;
      margin: 0 12px 12px 0;
    }
  }
}
</style>