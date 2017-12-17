'use strict';

/**
 * This is a pure no-op version of warning which is suitable for situations where you need
 * to transpile code to run in a browser.
 */

define(function (require, exports) {

     exports.warning = function () {};
   });
