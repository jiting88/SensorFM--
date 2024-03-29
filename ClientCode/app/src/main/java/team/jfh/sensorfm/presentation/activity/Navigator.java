/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package team.jfh.sensorfm.presentation.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class used to navigate through the application.
 */
@Singleton
public class Navigator {

  @Inject
  public Navigator() {
    //empty
  }

  /**
   * Goes to the user list screen.
   *
   * @param context A Context needed to open the destiny activity.
   */
  public void toMusicList(Context context) {
    if (context != null) {
      Intent intentToLaunch = MusicListActivity.getCallingIntent(context);
      context.startActivity(intentToLaunch);
    }
  }

  public void toLogin(Context context){
    if (context != null) {
      Intent intentToLaunch = LoginActivity.getCallingIntent(context);
        context.startActivity(intentToLaunch);
    }
  }

  public void toSignUp(Context context){
    if (context != null) {
      Intent intentToLaunch = SignUpActivity.getCallingIntent(context);
      context.startActivity(intentToLaunch);
    }
  }

  public void toMain(Context context){
    if(context!=null){
      Intent intentToLaunch=MainActivity.getCallingIntent(context);
      context.startActivity(intentToLaunch);
    }
  }

}
