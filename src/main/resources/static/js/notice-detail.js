$(function () {
    var bs = initialScrollBar();


})

function initialScrollBar() {
    var bs = BetterScroll.createBScroll('.wrapper',
        {
            pullUpLoad: true,
            /*wheel: {
                wheelWrapperClass: 'wheel-scroll',
                wheelItemClass: 'wheel-item',       wheel和mouseWheel搭配出现滚动不彻底的问题
                rotate: 0,
                adjustTime: 400,
                selectedIndex: 0,
                wheelDisabledItemClass: 'wheel-disabled-item'
            },*/ scrollY: true,
            scrollbar: {
                fade: true,
                interactive: false,
                // 以下配置项 v2.2.0 才支持
                customElements: [],
                minSize: 8,
                scrollbarTrackClickable: false,
                scrollbarTrackOffsetType: 'step',
                scrollbarTrackOffsetTime: 300,
                // 以下配置项 v2.4.0 才支持
                fadeInTime: 250,
                fadeOutTime: 500
            },
            mouseWheel: {
                speed: 20,
                invert: false,
                easeTime: 300,
                discreteTime: 400,
                throttleTime: 0,
                dampingFactor: 0.1
            }
        })
}
