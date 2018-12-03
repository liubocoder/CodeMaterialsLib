package com.lb.launcher

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import cn.lb.overrecycler.BaseHolderData
import cn.lb.overrecycler.BaseRecyclerAdapter
import cn.lb.overrecycler.SimpleAdapterHelper
import cn.lb.overrecycler.SimpleSpaceItemDecoration
import cn.lb.overrecycler.impl.IItemClickListener
import com.lb.launcher.list.ListIcTvData
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author LiuBo
 * @date 2018-11-21
 */
class MyLauncher : Activity(), IItemClickListener<BaseHolderData> {
    override fun onItemClick(v: View?, data: BaseHolderData?) {
        var info = data?.mOriData as ResolveInfo
        var pName = info.activityInfo.packageName
        var clsName = info.activityInfo.name
        var componentName = ComponentName(pName, clsName)

        var intent = Intent()
        intent.setComponent(componentName)

        startActivity(intent)
    }

    var mAdapter: BaseRecyclerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter = SimpleAdapterHelper.recyclerAdapter()

        mRecycler.layoutManager = GridLayoutManager(this, 4)
        mRecycler.addItemDecoration(SimpleSpaceItemDecoration(10))
        mRecycler.adapter = mAdapter

        /*var listData = arrayListOf<ListIcTvData>()

        for (i in 0..10) {
            var data = ListIcTvData()
            data.mDrawable = R.mipmap.ic_launcher
            data.mText = "xxx->"+i
            listData.add(data)
        }
        mAdapter?.updateAndNotifyData(listData)*/
        mAdapter?.updateAndNotifyData(getAllApp())
    }

    fun getAllApp() : List<ListIcTvData> {
        val pm = packageManager
        var listData = arrayListOf<ListIcTvData>()
        var intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        var listApp = pm.queryIntentActivities(intent, 0)
        var data:ListIcTvData
        listApp?.forEach{
            data = ListIcTvData()
            data.mDrawable = it.activityInfo.loadIcon(pm)
            data.mText = it.loadLabel(pm) as String
            data.mOriData = it
            data.mItemClickListener = this
            listData.add(data)
        }

        return listData
    }


}