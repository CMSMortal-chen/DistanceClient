//--- hatsuyuki ---
// by Catzpaw 2016
// edited by Mymylesaws
#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform float timeHelper;

float time = timeHelper;
uniform vec2 mouse;
uniform vec2 resolution;


void main( void )
{
	vec2 uv = gl_FragCoord.xy/ .6 / resolution.xy;
	vec3 wave_color = vec3(0.0);

	float wave_width = 0.0;
	uv  = -2.6 + 2.0 * uv;
	uv.y += 0.0;
	for(float i = 0.0; i <= 10.0; i++) {
		uv.y += (0.2+(0.8*sin(time*0.4) * sin(uv.x + i/3.0 + 3.0 *time)));
		uv.x += 1.7* sin(time*0.4);
		wave_width = abs(2.0 / (200.0 * uv.y));
		wave_color += vec3(wave_width *( 0.4+((i+1.0)/18.0)), wave_width * (i / 9.0), wave_width * ((i+1.0)/ 8.0) * 1.9);
	}

	gl_FragColor = vec4(wave_color, 1.0);
}
