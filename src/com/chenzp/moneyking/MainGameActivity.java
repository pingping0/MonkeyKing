package com.chenzp.moneyking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.ease.CCEaseExponentialIn;
import org.cocos2d.actions.ease.CCEaseIn;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItemImage;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.utils.CCFormatter;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainGameActivity extends Activity {

	protected CCGLSurfaceView _glSurfaceView;
	
	public static final String SCORETAG = "com.chenzp.moneyking.score";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		// ����ȫ����������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// ���ø�Activity��View
		_glSurfaceView = new CCGLSurfaceView(this);
		setContentView(_glSurfaceView);
	}


	@Override
	protected void onStart() {

		super.onStart();
		
		CCDirector.sharedDirector().attachInView(_glSurfaceView);

		CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
		
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);

		CCScene scene = GameLayer.scene();
		
		CCDirector.sharedDirector().runWithScene(scene);
	}

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// �����˳���
		case KeyEvent.KEYCODE_BACK:
			return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}


	public static void test()
	{
		Log.d("TEST", "�ݲŲݲ�");
		//Toast.makeText(this, "�ٰ�һ�������˳�������", Toast.LENGTH_SHORT).show()
	}

	/**
	 * �ڲ��࣬��ʾ��Ϸ������
	 * @author ����
	 *
	 */
	public static class GameLayer extends CCColorLayer {

		private CCSprite ground; // ���澫��
		
		private CCSprite monkey; // ���Ӿ���
		
		private CCSprite cloud; // �ƾ���

		private CCRepeatForever monkeryForever; // ����һֱ�ɵ�action
		
		private CCSpriteSheet spriteSheet; // ����������
		
		private List<CCSprite> visibleBads;   // �ɼ������֣�������ײ���
		
		private boolean gameOver = false;    // ��Ϸ�Ƿ�������涨δ��ʼʱΪfalse.
		
		private boolean isOn = false;   // ��Ϸ�Ƿ��ڽ�����

		private float time = 0; // ����������ʱ�䣬Ҳ���Ƿ���
		
		private CCEaseIn fall; // ���Ӻ������½��Ķ���(�������)
		
		private CCMoveBy sharpFall;  // ���Ӻ������½��Ķ���(��ײ��ļ����½���
		
		private long firstTime = 0; // ����ʵ�������������˳�
		/**
		 * ��Ļ��С
		 */
		private final CGSize WINSIZE;

		/**
		 * ��Ļ�е�λ��
		 */
		private final CGPoint MPOINT;
		
		/**
		 * ��ǰActivity
		 */
		private final Context app;
		
		// ���ڴ洢��߷���
		private SharedPreferences scorePreferences;
		
		/**
		 * ���һ�κ��������ĸ߶�
		 */
		public static final int  RISE = 60;
		
		/**
		 * ��ΪCCSpriteSheet spriteSheet��Tag��ʶ
		 */
		public static final int SHEETTAG = 1;

		/**
		 * ��Ϊ���ӵ�Tag��ʶ
		 */
		public static final int MONKEYTAG = 2;

		/**
		 * ��Ϊʱ���ǩ��Tag��ʶ
		 */
		public static final int TIMETAG = 3;
		
		/**
		 * ��Ϊ�����˵���Tag��ʶ
		 */
		public static final int POPTAG = 4;
		
		/**
		 * ��Ϊ������ǩ��Tag��ʶ
		 */
		public static final int SCORE = 5;
		
		/**
		 * ��Ϊ��߷ֵ�Tag��ʶ
		 */
		public static final int BEST = 6;
		
		/**
		 * ��̬����������GameLayer�ĳ���(Scene)
		 * 
		 * @return
		 */
		public static CCScene scene() {
			CCScene scene = CCScene.node();
			CCColorLayer layer = new GameLayer(ccColor4B.ccc4(255, 255, 255,
					255));
			scene.addChild(layer);
			return scene;
		}

		protected GameLayer(ccColor4B color) {
			super(color);

			// ���ø�Layer�Ĵ����¼���Ч
			this.setIsTouchEnabled(true);

			// ��ʼ������
			WINSIZE = CCDirector.sharedDirector().displaySize();
			MPOINT = CGPoint.ccp(WINSIZE.width / 2, WINSIZE.height / 2);
			visibleBads = new ArrayList<CCSprite>();
			app = CCDirector.sharedDirector().getActivity();
			// ƽ��1���½�280�߶�
			float fallDuration = WINSIZE.height / 280;
			// �½�
			CCMoveBy fallMoveBy = CCMoveBy.action(fallDuration, CGPoint.ccp(0, - WINSIZE.height));
			fall = CCEaseIn.action(fallMoveBy, 2.0f);
			scorePreferences = app.getSharedPreferences(SCORETAG, Context.MODE_PRIVATE);
			sharpFall = CCMoveBy.action(1.0f, CGPoint.ccp(0, -WINSIZE.height));
			
			//Ԥ������Ƶ
			SoundEngine.sharedEngine().preloadEffect(app, R.raw.click);
			SoundEngine.sharedEngine().preloadEffect(app, R.raw.bang);
			

			Log.d("TEST", WINSIZE.width+" -- "+WINSIZE.height);
			// ��Ϸ����
			CCSprite bg = CCSprite.sprite("bg.png");
			bg.setPosition(CGPoint.ccp(WINSIZE.width / 2, WINSIZE.height / 5 + 500));
			addChild(bg);
			
			// ��Ϸ����,ռ��Ļ 1/4��.��Ļ���ܸ���1000.
			ground = CCSprite.sprite("ground.png");
			ground.setPosition(CGPoint.ccp(WINSIZE.width / 2, WINSIZE.height / 5 - 100));
			addChild(ground,2);
			
			// �����ƶ�Ч��
			CCMoveBy moveBy1 = CCMoveBy.action(0.5f, CGPoint.ccp(-100, 0));
			CCMoveBy moveBy2 = CCMoveBy.action(0, CGPoint.ccp(100 , 0));
			CCRepeatForever repeatForever = CCRepeatForever.action(CCSequence.actions(moveBy1, moveBy2));
			ground.runAction(repeatForever);
			
			// �����Ǻ��Ӷ�����ʵ��
			// 1, ����֡����,ͨ������.plist�ļ�
			CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(
					"monkey_packer.plist");
			// 2, ��ʼ�����������飬ͨ������.png�ļ� (�°��CCSpriteSheet��CCSpriteBatchNode)
			spriteSheet = CCSpriteSheet
					.spriteSheet("monkey_packer.png");
			// 3. ����������������Ϊ��������ӽڵ�
			addChild(spriteSheet, 1, SHEETTAG);

			monkey = CCSprite.sprite("monkey1.png", true);
			monkey.setPosition(MPOINT);
			spriteSheet.addChild(monkey, 1, MONKEYTAG);
			
			
			

			// ֡����
			ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
			for (int i = 1; i < 5; i++) {
				String pngName = "monkey" + i + ".png";
				CCSpriteFrame frame = CCSpriteFrameCache
						.sharedSpriteFrameCache().spriteFrameByName(pngName);
				frames.add(frame);
			}

			CCAnimation animation = CCAnimation
					.animation("monkey", 0.1f, frames);
			CCAnimate animate = CCAnimate.action(animation, false);
			monkeryForever = CCRepeatForever.action(animate);

			monkey.runAction(monkeryForever);
			
			
			// ����
			cloud = CCSprite.sprite("cloud.png", true);
			cloud.setPosition(CGPoint.ccp(WINSIZE.width / 2, WINSIZE.height / 2 - 70));
			addChild(cloud);
			
			// ��ʼ����Ļ�˵�
		    CCSprite aboutSprite = CCSprite.sprite("about.png", true);
		    CCMenuItemSprite aboutItem = CCMenuItemSprite.item(aboutSprite, aboutSprite, this, "toAbout");
		    
		    CCSprite shareSprite = CCSprite.sprite("share.png", true);
		    CCMenuItemSprite shareItem = CCMenuItemSprite.item(shareSprite, shareSprite, this, "toShare");
		    
		    CCSprite exitSprite = CCSprite.sprite("exit.png", true);
		    CCMenuItemSprite exitItem = CCMenuItemSprite.item(exitSprite, exitSprite, this, "toExit");
		    
		    CCMenu screenMenu = CCMenu.menu(aboutItem,shareItem,exitItem);
		    screenMenu.alignItemsHorizontally(100);
		    screenMenu.setPosition(WINSIZE.width / 2 ,60);
		    addChild(screenMenu,3);
		    
		    // ��ʾʱ�䣬Ҳ���Ƿ���
		    CCBitmapFontAtlas timeLabel = CCBitmapFontAtlas.bitmapFontAtlas("0.0", "bitmapFontTest.fnt");
		    addChild(timeLabel,5,TIMETAG);
		    timeLabel.setScale(1.5f);
		    timeLabel.setPosition(CGPoint.ccp(150, WINSIZE.height - 100));
		    
		    // �����˵�,��ʼ������
		    CCSprite popMenu = CCSprite.sprite("pop_menu.png",true);
		    popMenu.setPosition(CGPoint.ccp(WINSIZE.width / 2, -150));
		    // ���¿�ʼ�˵���
		    CCSprite restartSprite = CCSprite.sprite("start.png", true);
		    CCMenuItemSprite restartItem = CCMenuItemSprite.item(restartSprite, restartSprite, this, "restart");
		    CCMenu reStartMenu = CCMenu.menu(restartItem);
		    popMenu.addChild(reStartMenu);
		    // �������¿�ʼ��ť��λ��
		    reStartMenu.setPosition(reStartMenu.getParent().getContentSize().width /2,
		    		reStartMenu.getParent().getContentSize().height /2);
		    
		    // ��������߷ֵı�ǩ
		    CCBitmapFontAtlas scoreLabel = CCBitmapFontAtlas.bitmapFontAtlas("0.0", "bitmapFontTest.fnt");
		    popMenu.addChild(scoreLabel,1,SCORE);
		    scoreLabel.setPosition(462, scoreLabel.getParent().getContentSize().height - 94); // �˴ν�λ��д���ˡ���ΪͼƬ��ϢΨһ����֪��
		    
		    CCBitmapFontAtlas bestLabel = CCBitmapFontAtlas.bitmapFontAtlas("0.0", "bitmapFontTest.fnt");
		    popMenu.addChild(bestLabel,1,BEST);
		    bestLabel.setPosition(462, bestLabel.getParent().getContentSize().height - 202);
		    
		    addChild(popMenu,1,POPTAG);
		    
		    
		    schedule("createBad", 1.5f); // �������ֵĵ���
		    
		    schedule("check");  // �����ײ�ĵ���
		    
		    
			
		}
	
		//��Ļ�˵��¼�����Ӧ
		
		// 1. ���ڲ˵�
		public void toAbout(Object sender){
			Log.d("TEXT", "���ڲ˵�����");
			System.out.println("���ڲ˵�����");
			Context context = CCDirector.sharedDirector().getActivity();
			SoundEngine.sharedEngine().playEffect(context, R.raw.click);
		}
		// 2. �����˵�
		public void toShare(Object sender){
			
		      Intent intent=new Intent(Intent.ACTION_SEND);
		      
		      intent.setType("text/plain");
		      intent.putExtra(Intent.EXTRA_SUBJECT, "share");
		      intent.putExtra(Intent.EXTRA_TEXT, "#Monkey King# ������һ��������Ϸ---Monkey King,С����ǿ����ذ�! ");
		      app.startActivity(Intent.createChooser(intent, "SHARE MONKEY KING"));
		      
		}
		
		// 3. �뿪�˵�
		public void toExit(Object sender){
		
			long secondTime = System.currentTimeMillis();
			
			if(secondTime - firstTime > 2000)
			{
				
				CCBitmapFontAtlas tipAtlas = CCBitmapFontAtlas.bitmapFontAtlas("Click one more to leave","bitmapFontTest.fnt");
				tipAtlas.setPosition(CGPoint.ccp(WINSIZE.width / 2, WINSIZE.height / 5 + 20));
				addChild(tipAtlas);
			    CCFadeIn fadeIn = CCFadeIn.action(1.5f);
			    CCFadeOut fadeOut = fadeIn.reverse();
			    tipAtlas.runAction(CCSequence.actions(fadeIn, fadeOut));
				firstTime = secondTime;
			}
			else {
				System.exit(0);
			}
			
		}
		
		// 4. ���¿�ʼ�˵�
		public void restart(Object sender){
			Log.d("TEST", "���¿�ʼ");
			
			// ������Ϊ0
			time = 0;
			CCBitmapFontAtlas label =
					(CCBitmapFontAtlas) getChildByTag(TIMETAG);
			label.setString("0.00");
			
			// �����Ļ�������
			spriteSheet.removeAllChildren(false);
			
			// �����˵��ص���Ļ֮�⣨��������
			CCSprite popSprite =
					(CCSprite) getChildByTag(POPTAG);
			popSprite.setPosition(CGPoint.ccp(WINSIZE.width / 2, -150));
			
			// �Ѻ��Ӻ��������·�����Ļ�м�
			monkey.setPosition(MPOINT);
			cloud.setPosition(CGPoint.ccp(WINSIZE.width / 2, WINSIZE.height / 2 - 70));
			
			spriteSheet.addChild(monkey, 1, MONKEYTAG);
			addChild(cloud);
			
			monkey.runAction(fall.copy());
			cloud.runAction(fall.copy());
			monkey.runAction(monkeryForever.copy());
			
		
			
			
			// �������ֵĵ���
			schedule("createBad", 1.5f);
			
			// ������Ϸ״̬
			isOn = true;
			gameOver = false;
			
		}
		
		// �������е�����
		public void createBad(float dt){
			// ����Ϸδ�����������У�ʱ
			if(isOn && !gameOver)
			{
				Random random = new Random();
				// ����������ֵ�����
				int badIndex = random.nextInt(6) + 1;
				CCSprite bad = CCSprite.sprite("bad"+badIndex+".png",true);
				
				// ����������ֵĳ�ʼλ��
				int badMinY =  (int) (bad.getContentSize().height / 2.0f + WINSIZE.height / 5);
				int badMaxY = (int) (WINSIZE.height - bad.getContentSize().height / 2.0f);
				int actualY = random.nextInt(badMaxY - badMinY) + badMinY;
				
				bad.setPosition(CGPoint.ccp(WINSIZE.width + 10, actualY));
				
			
				
				// ���ֵĶ���
				CCMoveBy moveBy = CCMoveBy.action(2.0f, CGPoint.ccp(-(110+WINSIZE.width), 0));
				CCCallFuncN removeBad = CCCallFuncN.action(this, "removeBad");
				CCSequence badSequence = CCSequence.actions(moveBy, removeBad);
				bad.runAction(badSequence);
				
				
				// ���뵽spriteSheet
				spriteSheet.addChild(bad);
	
				if(badIndex == 5 || badIndex == 1 || badIndex == 6)
				{
					// Ϊ�����Ϸ�Ѷȣ�����Ļ���ϲ���������
					int keyBadIndex = random.nextInt(6) + 1;
					CCSprite keyBad = CCSprite.sprite("bad"+keyBadIndex+".png", true);
					
					int actualKeyY = random.nextInt(150) + (int)WINSIZE.height - 150;
					keyBad.setPosition(CGPoint.ccp(WINSIZE.width + 10, actualKeyY));
					
					keyBad.runAction(badSequence.copy());
					spriteSheet.addChild(keyBad);
				}
			}
			
		}

		/**
		 * �ص������������������
		 * @param sender
		 */
		public void removeBad(Object sender){
			
			Log.d("TEST", "���ֱ��Ƴ�");
			spriteSheet.removeChild((CCSprite) sender, true);
		}
		
		/**
		 * �ص����������ڼ����ײ
		 * @param sender
		 */
		public void check(float dt){
			// ����Ϸ������ʱ�ż��
			if(isOn)
			{
				time += dt;
				// ���·���
				String string = CCFormatter.format("%2.3f", time / 10);
				CCBitmapFontAtlas label =
						(CCBitmapFontAtlas) getChildByTag(TIMETAG);
				label.setString(string);
				
				// ������ӵ������棨1/5 ��Ļ��)
				if(monkey.getPosition().y <= WINSIZE.height / 5)
				{
					isOn = false; 
					gameOver = true;
					spriteSheet.removeChild(monkey, false); // �Ƴ�����
					monkey.stopAllActions();
					removeChild(cloud, false);  // �Ƴ�����
					cloud.stopAllActions();
					ground.stopAllActions();  // ֹͣ������˶�
					
					// ����пɼ�������
					if(visibleBads.size() > 0)
					{
						// �ɼ������ֵ�ֹͣ�˶�
						for(CCSprite bad : visibleBads)
						{
							bad.stopAllActions();
						}
					}
					
					unschedule("createBad"); // ����������ֵĵ���
					
					// �����˵�����ʾ
					CCSprite popSprite =
							(CCSprite) getChildByTag(POPTAG);
					// �޸ķ�����ǩ
					CCBitmapFontAtlas scoreAtlas = (CCBitmapFontAtlas) popSprite.getChildByTag(SCORE);
					scoreAtlas.setString(CCFormatter.format("%2.1f", time / 10));
					// ��߷�
					float best = scorePreferences.getFloat(SCORETAG, 0.0f);
					
					// �����߷�С�ڵ�ǰ������������߷�
					if(best < (time / 10))
					{
						best = time / 10;
						// д�� SharedPreferences
						SharedPreferences.Editor editor = scorePreferences.edit();
						editor.putFloat(SCORETAG, best);
						editor.commit();
					}
					
					// �޸���߷ֱ�ǩ
					CCBitmapFontAtlas bestAtlas = (CCBitmapFontAtlas) popSprite.getChildByTag(BEST);
					bestAtlas.setString(CCFormatter.format("%2.1f", best));		
					
					popSprite.runAction(CCMoveTo.action(.2f, MPOINT));
					
					return;
				}
				
				// ����Ƿ����������
				visibleBads.clear();
				 
				List<CCNode> bads = spriteSheet.getChildren();
				
				// ���visibleBads
				if(bads != null && bads.size() > 0)
				{
					for(CCNode node : bads)
					{
						CCSprite sprite = (CCSprite) node;
						if(sprite.getTag() != MONKEYTAG)
						{
							visibleBads.add(sprite);
						}
					}
				}
				
				// �����ײ
				if(visibleBads.size() > 0)
				{
					for(CCSprite sprite : visibleBads)
					{
						// ��������ײ
						if(CGRect.intersects(monkey.getBoundingBox(), sprite.getBoundingBox()))
						{
							SoundEngine.sharedEngine().playEffect(app, R.raw.bang);
							gameOver = true;
							monkey.runAction(sharpFall.copy());
							cloud.runAction(sharpFall.copy());
							
						}
					}
				}
				 
			}
		}
		
		/**
		 * ��д�����¼�
		 */
		@Override
		public boolean ccTouchesBegan(MotionEvent event) {
			
			if(gameOver) return true; // �����Ϸ�����ˣ�����������),����Ļ������Ч
			
			// ����isOn��ʼֵΪfalse,ͨ������������Ϸ
			if(!isOn) isOn = true; 
			
			// ��Ч
			SoundEngine.sharedEngine().playEffect(app, R.raw.click);
			
			float monkeyY = monkey.getPosition().y;
			
			// ʵ�ʵ������߶ȣ���ֹ������Ļ
			float acturalRise =( (monkeyY + RISE) > WINSIZE.height - 20)? (WINSIZE.height - 20 - monkeyY):RISE;
			
			// ����
			CCMoveBy riseMoveBy = CCMoveBy.action(.1f, CGPoint.ccp(0, acturalRise));
			
			// �����������½�
			CCSequence sequence = CCSequence.actions(riseMoveBy, fall.copy());
			
			// ֹͣ���Ӻ����ŵĶ���
	        monkey.stopAllActions();
	        cloud.stopAllActions();
	        
	        // ����󣬺��Ӻ�����ͬ�����������½�
	        monkey.runAction(sequence);
	        cloud.runAction(sequence.copy());
	        
	        // ���ӵķ��ж���
	        monkey.runAction(monkeryForever.copy());
	        
			return super.ccTouchesBegan(event);
		}
		
		
		
	}
	
	


}