package com.Fliter;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * description:趣拍本module常量
 *
 * @author: 罗明颖
 * @Create Time:2018/7/23
 */

public interface PatRecordConstant {
    /**
     * 商汤模型
     */

    interface IntentExtra {
        /**
         * 选择的音乐id
         */
        String EXTRA_SELECT_MUSIC_ID = "extra_select_music_id";
        /**
         * 选择的音乐本地路径
         */
        String EXTRA_SELECT_MUSIC_PATH = "extra_select_music_path";
//        /**
//         * 音乐名称
//         */
//        String EXTRA_SELECT_MUSIC_NAME = "extra_select_music_name";
//        /**
//         * 音乐收藏状态
//         */
//        String EXTRA_SELECT_MUSIC_COLLECT = "extra_select_music_collect";
        /**
         * 音乐选择bean
         */
        String EXTRA_SELECT_MUSIC = "extra_selct_music";


        /**
         * 分类id
         */
        String EXTRA_CATEGORY_ID = "extra_category_id";
        /**
         * 分类名
         */
        String EXTRA_CATEGORY_NAME = "extra_category_name";
        /**
         * 素材id
         */
        String EXTRA_MATERIAL_ID = "extra_material_id";

        /**
         * 素材更新时间
         */
        String EXTRA_MATERIAL_UPDATE_TIME = "extra_material_update_time";

        /**
         * 素材type
         */
        String EXTRA_MATERIAL_TYPE = "extra_material_type";
        /**
         * 素材视频封面
         */
        String EXTRA_MATERIAL_COVER = "extra_material_cover";
        /**
         * 素材视频时长
         */
        String EXTRA_MATERIAL_DURATION = "extra_material_duration";

        /**
         * model
         */
        String EXTRA_MODEL = "MODEL";
        /**
         * 音乐搜索名
         */
        String EXTRA_SEARCH_MUSIC_NAME = "extra_search_music_name";

    }

    interface ResultCode {
        /**
         * 音乐选择回调code
         */
        int RESULT_CODE_SELECT_MUSIC = 100;
    }

    interface MusicRecommendType {
        /**
         * 热门
         */
        int HOT = 1;
        /**
         * 收藏
         */
        int COLLECTION = 2;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef(value = {HOT, COLLECTION})
        @interface Val {
        }
    }

    interface PatRecordParamsType {


        /**
         * --------------趣拍相关配置数据 start-------------------
         */

        public static final int MSG_SAVING_IMG = 1;
        public static final int MSG_DRAW_OBJECT_IMAGE_AND_RECT = 3;
        public static final int MSG_DRAW_OBJECT_IMAGE = 4;
        public static final int MSG_CLEAR_OBJECT = 5;
        public static final String SETTING_JSON = "setting.json";
        public final static int MSG_UPDATE_HAND_ACTION_INFO = 100;
        public final static int MSG_RESET_HAND_ACTION_INFO = 101;
        public final static int MSG_UPDATE_BODY_ACTION_INFO = 102;
        public final static int MSG_UPDATE_FACE_EXPRESSION_INFO = 103;

        public static String PATMATERIALTYPE = "patMaterialType";
        public static String PATMATERIALID = "patMaterialId";
        public static String PATMATERIALFACEINID = "patFaceInMaterialId";
        public static String REC_WORK_FOLDER = "tmpCrop";
        //舞蹈路径
        public static String REC_WORK_FOLDER_DANCE = "tmpCropDance";

        public static String KEY_IS_FROM_DRAFT = "FROM_DRAFT";
        public static String PARAMS_LOCAL = "pat_loacl_video";
        public static String PARAMS_MUSIC = "pat_loacl_music";
        public static String PARAMS_MIRROR_STAUTS = "pat_mirror_status";

        public static String PARAMS_FILTER_FOLDER = "filter_material";
        /**
         * 录制参数
         */
        public static String PARAMS_JSON = "params.json";
        /**
         * 外部拉起参数
         */
        public static String ENTRY_PARAMS_JSON = "entryParam.json";

        /**
         * 场景恢复参数
         */
        public static String SCENE_RECOVERY_PARAMS_JSON = "scene_recovery_params.json";

        /**
         * 本地转码参数
         */
        public static String PARAMS_JSON_LOCAL_VIDEO = "params_local_video.json";
        public static String PARAMS_VIDEO_COVER = "/F_0.jpg";

        /**
         * 录制临时文件名
         */
        public static String DRAFT_VIDEO_NAME = "mgtmp.mp4";
        /**
         * 录制后生成的文件名
         */
        public static String DRAFT_VIDEO_FINAL_NAME = "final.mp4";

        public static String TYPEPAT = "type_pat";
        public static String TYPEPATID = "type_pat_id";
        public static String TYPEPATIDTITLE = "type_pat_id_title";
        public static String VIDEO_FILE = "video_file";
        public static String EDIT_WORK_FOLDER = "edit_work_foloder";
        public static String MIRROR_VIDEO_PATH = "mirror_video_path";
        public static String MIRROR_MUSIC_PATH = "mirror_music_path";
        public static String MIRROR_PLAY_SCRIPT = "play_script.json";
        public static String MIRROR_PARTVIDEO = "partVideo";
        public static String MIRROR_MEDIA = "media";
        public static String MIRROR_RESOURCE_PATH = "mirror_resource_path";
        public static String MIRROR_MASK_PATH = "mirror_mask_path";
        public static String MIRROR_IS_MASK = "mirror_is_mask";
        public static String MIRROR_IS_PREPARE = "mirror_is_prepare";
        public static String VIDEO_TIME = "video_time";
        public static String MUSIC_FILE = "music_file";
        public static String IS_DRAFT = "is_draft";
        public static String MAX_DURATION = "max_duration";
        public static String IS_VERTICAL = "is_vertical";
        public static String MIRROR_DURATIRON = "mirror_duration";
        public static String MIRROR_SIZE_VIDEOW = "mirror_size_videow";
        public static String MIRROR_SIZE_VIDEOH = "mirror_size_videoh";
        public static String MIRROR_SIZE_VIDEOWH = "mirror_size_videowh";
        public static String MIRROR_SIZE_VIDEOWHS = "mirror_size_videowhs";
        public static String MIRROR_MODE = "mirror_mode";
        public static String MIRROR_VH = "mirror_VH";
        public static String MIRROR_INDEX = "mirror_index";
        public static String MIRROR_RECT = "mirror_rect";
        public static String MIRROR_INTO_RECT = "mirror_into_rect";
        public static String MIRROR_ARRRY = "mirror_array";
        public static String MIRROR_LOCAL_VIDEO_SIZE = "mirror_local_video_size";
        public static String MIRROR_ARRRY_VIDEO_PATH = "mirror_array_video_path";
        public static String MIRROR_ARRRY_VIDEO_DURATION = "mirror_array_video_duration";
        public static String MIRROR_ARRRY_VIDEO_LOCUAL = "mirror_array_video_locual";
        public static String MIRROR_ARRRY_ID = "mirror_array_id";
        public static String DRAFT_STATUS = "draft_status";
        public static String MUSIC_FILE_NAME = "music_file_path";
        public static int PAT_MUSIC = 01;
        public static int PAT_COVER = 02;
        public static int PAT_COVER_SUCCESS = 03;
        public static int PAT_FRIEND_CODE = 04;
        public static int PAT_CHANNEL_CODE = 05;
        public static int PAT_RECORD_MUSIC = 06;
        public static int PAT_SEARCH_FRIEND = 00011;
        public static String PAT_COVER_START_TIME = "pat_cover_start_time";
        public static String TMP_COVER_FOLDER = "tmp_cover";
        public static String PAT_COVER_FOLDER = "pat_cover";
        public static String Pat_COVER_PATH = "pat_cover_path";
        public static String Pat_PUBLIC_FRIEND_NAME = "pat_public_friend_name";
        public static String Pat_PUBLIC_FRIEND_NAME_ID = "pat_public_friend_name_id";
        public static String VIDEO_DRAFT_DIR_NAME = "draft";
        public static String VIDEO_CONTROL_COUNT = "conrol_count";
        public static String LOCAL_VIDEO_INFO = "local_video_info";
        public static String LOCAL_VIDEO_STARTTIME = "local_video_start_time";
        public static String LOCAL_VIDEO_ENDTIME = "local_video_end_time";
        public static String LOCAL_VIDEO_CUT_CHANGE = "local_video_cut_change";
        public static String LOCAL_VIDEO_CUT_STATUS = "local_video_cut_status";
        public static String LOCAL_NEW_VIDEO_CUT_STATUS = "local_new_video_cut_status";
        public static String LOCAL_VIDEO_THUMB_FOLDER = "local_video_thumb_folder";
        public static String PAT_TAKE_PHOTO = "pat_face.jpg";
        public static String PAT_TAKE_PHOTO_MATERIALID = "pat_material_id";
        public static String PAT_TAKE_PHOTO_MATERIALCOVERURL = "pat_material_cover_url";
        public static String PAT_TAKE_PHOTO_MATERIALDURATION = "pat_material_cover_duration";
        public static String PAT_CHALLENGE_TITLE = "pat_challenge_title";
        public static String PAT_EDIT_PHOTO = "edit_face.jpg";
        public static String PAT_TMP_DOWNVIDEO = "tmp_video.mp4";
        public static final int MSG_UPDATE_PROGRESS = 0x0009;
        public static String PAT_CHALLENGE_ID = "challenge_id";
        public static int PAT_ADD_CHALLENGE_ID = 0x0010;
        public static String PAT_CHALLENGE_NAME = "challenge_name";
        public static String PAT_CHALLENGE_DESCRIPTION = "challenge_description";
        public static String PAT_STICK = "stickFolder";
        public static int PAT_PICK_PHOTO = 0x0011;
        public static int PAT_STICK_MATERIAL = 1;//贴纸
        public static int PAT_FACE_MATERIAL = 2;//人脸
        public static int PAT_FACE_DANCER = 3;//舞蹈
        public static int PAT_SPLIT_MIRROR = 4;//分鏡
        public static String PAT_HISTORY_DATA = "pat_history_data";//历史数据
        public static String PAT_BROCALS_FINISH = "pat_finish";//关闭acitivty广播
        public static String PAT_BROCALS_FINISH_MIRROR = "pat_finish_mirror";//关闭acitivty广播
        public static String PAT_BROCALS_DATA_MIRROR = "pat_mirror_data";//分镜数据传递
        public static String PAT_HISTORY_DATA_KEY = "pat_history_data_key";//历史数据
        public static String PAT_TMP_CUT_DATA_THUMB = "pat_cut_tmp_thumb"; //视频裁剪页面临时缩略图

        public static final boolean HAS_FACETAB = false;
        public static final int PAT_MATEIRAL_RECENT = 0;
        public static final int PAT_MATEIRAL_COS = 1;
        public static final int PAT_MATEIRAL_FACE = 2;
        public static final int PAT_MATEIRAL_DANCER = 3;
        public static final int PAT_MATEIRAL_SPLITMIRROR = 4;
        public static final int PAT_MATEIRAL_OTHER = 5;

        //编辑页裁剪
        public static final int PAT_EDIT_EFFECT = 1;
        public static final int PAT_EDIT_COVER_RANGE = 2;
        public static final int PAT_EDIT_BG_MUSIC = 3;
        public static final int PAT_EDIT_BG_VOLUM = 4;
        public static final int PAT_EDIT_MIRROR = 5;


        public static final int PAT_MATEIRAL_THUMB_COUNT = 10;//缩略图数量
        public static final int PAT_MATEIRAL_TIME_LEAST = 10;//最少10秒
        public static final int PAT_MATEIRAL_TIME_MAX = 60 * 1000;//最长录制时间

        public static final int PAT_RECORD_SLOW = 0;//录制速度 慢
        public static final int PAT_RECORD_NORMAL = 1;//录制速度 标准
        public static final int PAT_RECORD_FAST = 2;//录制速度 快

        /**
         * 合成的视频非全I帧 只适用于本地视频裁剪
         */
        public static String DRAFT_VIDEO_PAT_NAME = "patVideo.mp4";
        /**
         * 合成的视频全I帧
         */
        public static String DRAFT_VIDEO_PAT_NAMEI = "patVideoI.mp4";

        public static final int WIDTH = 720;//视频宽
        public static final int HEIGHT = 1280;//视频高


        /**--------------趣拍相关配置数据 end-------------------
         */

        /**
         * 用户认证类型,0-非官方 1-官方 2-作者 3-coser 4-声优 5-大V账号 6-唱见 7-舞见 8-明星UP主 9-客服
         */
        public interface OfficialType {
            /**
             * 普通，非官方
             */
            int NORMAL = 0;
            /**
             * 官方
             */
            int OFFICIAL = 1;

            /**
             * 作者
             */
            int AUTHOR = 2;

            /**
             * coser
             */
            int COSER = 3;

            /**
             * 声优
             */
            int VOICE_ACTOR = 4;

            /**
             * 大V账号
             */
            int V_AUTHOR = 5;
            /**
             * 唱见
             */
            int SEE_SING = 6;
            /**
             * 舞见
             */
            int DANCE_SING = 7;
            /**
             * 明星UP主
             */
            int STAR_UP = 8;

            /**
             * 客服
             */
            int KEFU = 9;

            /**
             * 达人
             */
            int CELEBRITY = 10;
        }

        //--------------------  趣拍  start--------------------
        /**
         * 趣拍是否拍摄过
         */
        public static final String HASRECORD = "pat_has_record";
        /**
         * 录制页面相机状态
         */
        public static final String RECORD_CARMER_STATUS = "record_carmer_status";
        /**
         * 是否已弹出过趣拍拍摄引导图
         */
        public static final String HAS_NEWER_RECORD = "has_newer_record";
        /**
         * 是否已弹出过趣拍拍摄工具栏引导图
         */
        public static final String HAS_NEWER_RECORD_UTILS = "has_newer_record_utils";
        /**
         * 拍照页面相机状态
         */
        public static final String TAKE_PIC_CARMER_STATUS = "take_pic_carmer_status";

        public static final String PAT_PAUSE_SPLIST = "pat_pause_splist";
        public static final String PAT_CURRENT_PROGRESS = "pat_progress";
        public static final String PAT_CURRENT_MATERIAL = "pat_material";

        /**
         * 下载状态正常状态
         */
        public static final int DOWNLOAD_STATUS_NORMAL = 0;
        /**
         * 下载状态下载中状态
         */
        public static final int DOWNLOAD_STATUS_DOANLOADING = 1;
        /**
         * 下载状态下载完毕状态
         */
        public static final int DOWNLOAD_STATUS_COMPLETE = 2;

        /**
         * 古德SDK 配置文件 data.txt
         */
        public static final String GDPARAMS = "data.txt";
        /**
         * 拷贝古德SDK配置文件 data.txt 为 datacopy.txt
         */
        public static final String GDPARAMS_COPY = "datacopy.txt";
        /**
         * 录制参数
         */
        public static String PARAMS_JSON_COPY = "paramscopy.json";
        public static final String PAT_MUSIC_VOLUM = "pat_record_music_volum";//音乐音量
        public static final String PAT_VIDEO_VOLUM = "pat_record_video_volum";//视频音量
        /**
         * 商汤滤镜
         */
        public static final String PAT_FILTER_ADEN_ = "filter_style_aden_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_AL_ = "filter_style_al_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_BABYPINK_ = "filter_style_babypink.model";
        public static final String PAT_FILTER_CHERRY_ = "filter_style_cherry_1.5.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_CR_ = "filter_style_cr_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_MARGARET_ = "filter_style_margaret_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_MODERN_ = "filter_style_modern_1.5.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_OL_ = "filter_style_ol.model";
        public static final String PAT_FILTER_P5_ = "filter_style_p5_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_PEACH = "filter_style_peach_1.5.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_RUBH_ = "filter_style_ruby_1.5.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_START_ = "filter_style_star_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_UENO_ = "filter_style_ueno_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_WALLSTREET = "filter_style_wallstreet_1.4.0_v2_origin_20181210_20190115.model";
        public static final String PAT_FILTER_WATERFALL_ = "filter_style_waterfall_1.4.0_v2_origin_20181210_20190115.model";

        //拍摄美颜工具栏
        final int DEFAULT_LVJING = 6;//默认选中index=6 粉嫩
        final int DEFAULT_MOPI = 60;//磨皮60%
        final int DEFAULT_SHOULIAN = 60;//瘦脸60%

        //--------------------  趣拍  end-------------------
    }

    /**
     * 音乐选择目录
     */
    interface MusicSelectStorage {
        /**
         * 使用的音乐下载存放 上级目录
         */
        String SELECT_DOWN_LOAD_DIRECT = "SelectDownLoad";
    }

    /**
     * desc:缓存key
     * <p>
     * author:罗明颖
     * date:2018/11/25
     */
    interface CacheKey {
        String MUSIC_SELECTED = "music_selected";
    }
}
