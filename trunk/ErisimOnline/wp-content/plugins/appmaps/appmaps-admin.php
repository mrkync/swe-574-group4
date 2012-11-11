<?php
  if (!current_user_can('manage_options')) {
    wp_die( __('You do not have sufficient permissions to access this page.') );
  }

  //Update options
  if( isset( $_POST['options_update']) ) {
    update_option('appmaps_active', $_POST['appmaps_active']);
    update_option('appmaps_lat', $_POST['appmaps_lat']);
    update_option('appmaps_lng', $_POST['appmaps_lng']);
  	update_option('appmaps_gmaps_lang', $_POST['appmaps_gmaps_lang']);
  	update_option('appmaps_gmaps_region', $_POST['appmaps_gmaps_region']);
    
    echo '<div class="updated"><p><strong>' . __('Settings saved', 'appmaps') . '</strong></p></div>';
  }


	?>	
<script type="text/javascript">
// <![CDATA[
  jQuery(document).ready(function() {
    jQuery("#tabs-wrap").tabs({fx: {opacity: 'toggle', duration: 200}});
  });
// ]]>
</script>
<div class="wrap">
  <div class="icon32" id="icon-options-general"><br /></div>
  <h2><?php _e('General Settings', 'appmaps'); ?></h2>
  <form name="mainform" method="post" action="">
    <input type="hidden" value="1" name="options_update">

    <div id="tabs-wrap" class="">
      <ul class="tabs">
        <li class=""><a href="#tab1"><?php _e('General', 'appmaps'); ?></a></li>
      </ul>
      
      <div id="tab1" class="">
        <table class="widefat fixed" style="width:850px; margin-bottom:20px;">
          <thead>
            <tr>
              <th width="200px" scope="col"><?php _e('General', 'appmaps'); ?></th>
              <th scope="col">&nbsp;</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><?php _e('Activate?', 'appmaps'); ?></td>
              <td>
                <select name="appmaps_active">
                  <option value="no" <?php if(get_option('appmaps_active') == 'no'){ echo 'selected="selected"'; } ?> ><?php _e('No', 'appmaps'); ?></option>
                  <option value="yes" <?php if(get_option('appmaps_active') == 'yes'){ echo 'selected="selected"'; } ?> ><?php _e('Yes', 'appmaps'); ?></option>
                </select>
                <br /><small><?php _e('If "YES" is selected, then plugin will add a map to edit listing page.', 'mnet-langbf'); ?></small>
              </td>
            </tr>
            <tr>
              <td><?php _e('Default latitude', 'appmaps'); ?></td>
              <td>
                <input type="text" value="<?php echo get_option('appmaps_lat'); ?>" style="min-width:500px;" id="appmaps_lat" name="appmaps_lat" /><br />
                <small><?php _e('Default latitude on the map.', 'appmaps'); ?></small>
              </td>
            </tr>
            <tr>
              <td><?php _e('Default longitude', 'appmaps'); ?></td>
              <td>
                <input type="text" value="<?php echo get_option('appmaps_lng'); ?>" style="min-width:500px;" id="appmaps_lng" name="appmaps_lng" /><br />
                <small><?php _e('Default longitude on the map.', 'appmaps'); ?></small>
              </td>
            </tr>
            <tr>
              <td><?php _e('Google Maps Language', 'appmaps'); ?></td>
              <td>
                <input type="text" value="<?php echo get_option('appmaps_gmaps_lang'); ?>" style="min-width:500px;" id="appmaps_gmaps_lang" name="appmaps_gmaps_lang" /><br />
                <small><?php _e('Find the list of supported language codes', 'appmaps'); ?> <a href="http://spreadsheets.google.com/pub?key=p9pdwsai2hDMsLkXsoM05KQ&gid=1" target="_new" title=""><?php _e('here','appmaps') ?></a>.</small>
                <br /><small><?php _e('The Google Maps API uses the browsers language setting when displaying textual info on the map. In most cases, this is preferable and you should not need to override this setting. However, if you wish to change the Maps API to ignore the browsers language setting and force it to display info in a particular language, enter your two character region code here (i.e. Japanese is ja).', 'appmaps'); ?></small>
              </td>
            </tr>
            <tr>
              <td><?php _e('Google Maps Region', 'appmaps'); ?></td>
              <td>
                <input type="text" value="<?php echo get_option('appmaps_gmaps_region'); ?>" style="min-width:500px;" id="appmaps_gmaps_region" name="appmaps_gmaps_region" /><br />
                <small><?php _e('Find your two-letter ISO 3166-1 region code', 'appmaps'); ?> <a href="http://en.wikipedia.org/wiki/ISO_3166-1" target="_new" title=""><?php _e('here','appmaps') ?></a>.</small>
                <br /><small><?php _e('Enter your country\'s two-letter region code here to properly display map locations. (i.e. Someone enters the location "Toledo", it\'s based off the default region (US) and will display "Toledo, Ohio". With the region code set to "ES" (Spain), the results will show "Toledo, Spain.")', 'appmaps'); ?></small>
              </td>
            </tr>
          </tbody>
        </table>

      </div>


      <p class="submit">
        <input type="submit" name="Submit" class="button-primary" value="<?php _e('Save Changes', 'appmaps'); ?>" />
      </p>

    </div>    
  </form>

</div>    
<div class="clear"></div>